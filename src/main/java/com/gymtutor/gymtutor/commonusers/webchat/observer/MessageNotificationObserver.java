package com.gymtutor.gymtutor.commonusers.webchat.observer;

import com.gymtutor.gymtutor.commonusers.webchat.MessageDTO;
import com.gymtutor.gymtutor.commonusers.webchat.MessageModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Observer concreto — reage ao envio de mensagens.
 */
@Component
public class MessageNotificationObserver implements MessageObserver {

    private final SimpMessagingTemplate messagingTemplate;

    public MessageNotificationObserver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessageSent(MessageModel messageModel) {
        MessageDTO dto = new MessageDTO(messageModel);

        // Envia para o destinatário e o remetente via WebSocket
        messagingTemplate.convertAndSendToUser(
                messageModel.getReceiver().getUserEmail(),
                "/queue/messages",
                dto
        );

        messagingTemplate.convertAndSendToUser(
                messageModel.getSender().getUserEmail(),
                "/queue/messages",
                dto
        );
    }
}
