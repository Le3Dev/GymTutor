package com.gymtutor.gymtutor.commonusers.webchat.observer;

import com.gymtutor.gymtutor.commonusers.webchat.MessageModel;

/**
 * Interface Observer — Define o contrato para todos os observadores
 * que desejam ser notificados quando uma nova mensagem é enviada.
 */
public interface MessageObserver {
    /**
     * Metodo chamado automaticamente sempre que o Subject notifica seus observadores.
     * @param messageModel Mensagem que foi enviada.
     */
    void onMessageSent(MessageModel messageModel);
}
