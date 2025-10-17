package com.gymtutor.gymtutor.commonusers.webchat;

/**
 * Interface Subject — Define o contrato para qualquer objeto que possa
 * ser observado (ou seja, que notifica observadores).
 */
public interface MessageSubject {
    /**
     * Adiciona um observador à lista de observadores registrados.
     * @param observer Instância que será notificada.
     */
    void addObserver(MessageObserver observer);

    /**
     * Notifica todos os observadores registrados sobre um novo evento.
     * @param messageModel A mensagem recém-enviada.
     */
    void notifyObservers(MessageModel messageModel);
}
