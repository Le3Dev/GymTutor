package com.gymtutor.gymtutor.commonusers.webchat;

import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    // Abre página do Chat
    @GetMapping("/chat")
    public String openChatPage(
            @RequestParam(name = "otherUserId", required = false) Integer otherUserId,
            Model model,
            Principal principal
    ) {
        User currentUser = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Busca conversas onde currentUser é user1 ou user2
        List<Conversation> convUser1 = conversationRepository.findByUser1(currentUser);
        List<Conversation> convUser2 = conversationRepository.findByUser2(currentUser);

        // Junta as duas listas
        List<Conversation> allConversations = new ArrayList<>();
        allConversations.addAll(convUser1);
        allConversations.addAll(convUser2);

        // Ordena por data da última mensagem da conversa (desc)
        allConversations.sort((c1, c2) -> {
            LocalDateTime lastMsg1 = c1.getMessages().stream()
                    .map(m -> m.getTimestamp())
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            LocalDateTime lastMsg2 = c2.getMessages().stream()
                    .map(m -> m.getTimestamp())
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            return lastMsg2.compareTo(lastMsg1); // DESC
        });

        // Extrai os contatos (usuários que não são o currentUser)
        List<User> activeContacts = allConversations.stream()
                .map(c -> c.getUser1().equals(currentUser) ? c.getUser2() : c.getUser1())
                .toList();

        // Lista todos usuários para nova conversa
        List<User> allUsers = userRepository.findAll().stream()
                .filter(user -> !user.equals(currentUser))
                .toList();

        User selectedUser = null;
        if (otherUserId != null) {
            selectedUser = userRepository.findById(otherUserId).orElse(null);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeContacts", activeContacts);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("selectedUser", selectedUser); // ID do usuário do "Enviar Mensagem"
        model.addAttribute("body", "webchat/webchat");
        return "/fragments/layout";
    }

    // Retorna a conversa entre o usuário logado e outro usuário
    @GetMapping("/api/conversations/{otherUserId}")
    @ResponseBody
    public List<MessageDTO> getConversation(@PathVariable Integer otherUserId, Principal principal) {
        User currentUser = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Usuário de destino não encontrado"));

        return conversationRepository.findBetweenUsers(currentUser, otherUser)
                .map(Conversation::getMessages)
                .orElse(List.of())
                .stream()
                .map(MessageDTO::new)
                .toList();
    }

    // Processa e envia a mensagem via WebSocket
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO, Principal principal) {
        // Verifica se receiver existe no DTO
        if (messageDTO.getReceiver() == null || messageDTO.getReceiver().getUserId() == null) {
            throw new IllegalArgumentException("Receiver não pode ser nulo");
        }

        // Busca o usuário autenticado (sender)
        User sender = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Busca o receiver pelo ID vindo no DTO
        User receiver = userRepository.findById(messageDTO.getReceiver().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver com ID " + messageDTO.getReceiver().getUserId() + " não encontrado"));

        // Cria a entidade MessageModel
        MessageModel messageModel = new MessageModel();
        messageModel.setContent(messageDTO.getContent());
        messageModel.setTimestamp(LocalDateTime.now());
        messageModel.setSender(sender);
        messageModel.setReceiver(receiver);

        // Busca ou cria conversa
        Conversation conversation = conversationRepository
                .findBetweenUsers(sender, receiver)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setUser1(sender);
                    newConv.setUser2(receiver);
                    return conversationRepository.save(newConv);
                });

        messageModel.setConversation(conversation);
        messageRepository.save(messageModel);

        //Padrão Observer
        MessagePublisher publisher = new MessagePublisher();
        publisher.addObserver(new MessageNotificationObserver(messagingTemplate));
        publisher.notifyObservers(messageModel);
    }
}