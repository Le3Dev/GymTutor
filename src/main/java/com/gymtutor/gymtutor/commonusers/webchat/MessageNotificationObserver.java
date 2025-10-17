package com.gymtutor.gymtutor.commonusers.webchat;

import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Classe ConcreteObserver — Implementa a interface Observer.
 * Responsável por enviar a mensagem via WebSocket sempre que
 * for notificado de uma nova mensagem.
 */
public class MessageNotificationObserver implements MessageObserver {

    private final SimpMessagingTemplate messagingTemplate;

    public MessageNotificationObserver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessageSent(MessageModel messageModel) {
        MessageDTO dto = new MessageDTO(messageModel);

        // Envia ao destinatário
        messagingTemplate.convertAndSendToUser(
                messageModel.getReceiver().getUserEmail(),
                "/queue/messages",
                dto
        );

        // Envia também ao remetente
        messagingTemplate.convertAndSendToUser(
                messageModel.getSender().getUserEmail(),
                "/queue/messages",
                dto
        );
    }
}