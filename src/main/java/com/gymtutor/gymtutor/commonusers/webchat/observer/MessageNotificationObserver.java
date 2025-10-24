package com.gymtutor.gymtutor.commonusers.webchat.observer;

import com.gymtutor.gymtutor.commonusers.webchat.MessageModel;
import com.gymtutor.gymtutor.commonusers.webchat.WebSocketSender;
import org.springframework.stereotype.Component;

/**
 * Observer concreto — reage ao envio de mensagens.
 */
@Component
public class MessageNotificationObserver implements MessageObserver {

    private final WebSocketSender webSocketSender;

    public MessageNotificationObserver(WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
    }

    @Override
    public void onMessageSent(MessageModel messageModel) {
        // Envia a mensagem via WebSocket diretamente
        webSocketSender.send(messageModel);
    }
}
