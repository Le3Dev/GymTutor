package com.gymtutor.gymtutor.commonusers.webchat;

import com.gymtutor.gymtutor.commonusers.webchat.MessageDTO;
import com.gymtutor.gymtutor.commonusers.webchat.MessageModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por enviar mensagens via WebSocket diretamente.
 */
@Component
public class WebSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envia a mensagem para o destinatário e remetente via WebSocket.
     */
    public void send(MessageModel messageModel) {
        MessageDTO dto = new MessageDTO(messageModel);

        // Envia para o destinatário
        messagingTemplate.convertAndSendToUser(
                messageModel.getReceiver().getUserEmail(),
                "/queue/messages",
                dto
        );

        // Envia para o remetente
        messagingTemplate.convertAndSendToUser(
                messageModel.getSender().getUserEmail(),
                "/queue/messages",
                dto
        );
    }
}
