package com.gymtutor.gymtutor.commonusers.webchat;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe ConcreteSubject — Implementa a interface Subject.
 * Responsável por manter a lista de observadores e notificá-los
 * sempre que uma nova mensagem é enviada.
 */
public class MessagePublisher implements MessageSubject {

    private final List<MessageObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(MessageObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(MessageModel messageModel) {
        for (MessageObserver observer : observers) {
            observer.onMessageSent(messageModel);
        }
    }
}
