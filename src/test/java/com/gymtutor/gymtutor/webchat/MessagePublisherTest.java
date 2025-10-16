package com.gymtutor.gymtutor.webchat;
import com.gymtutor.gymtutor.commonusers.webchat.MessageModel;
import com.gymtutor.gymtutor.commonusers.webchat.MessageObserver;
import com.gymtutor.gymtutor.commonusers.webchat.MessagePublisher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class MessagePublisherTest {

    @Test
    void deveNotificarObserverQuandoMensagemEnviada() {
        MessagePublisher publisher = new MessagePublisher();

        // Cria um observer falso (mock manual)
        final boolean[] foiNotificado = {false};
        MessageObserver observer = message -> {
            foiNotificado[0] = true;
            System.out.println("Observer recebeu mensagem: " + message.getContent());
        };

        publisher.addObserver(observer);

        // Cria mensagem simulada
        MessageModel msg = new MessageModel();
        msg.setContent("Mensagem teste");

        // Notifica
        publisher.notifyObservers(msg);

        // Verifica se o observer foi notificado
        assertTrue(foiNotificado[0], "O observer deveria ter sido notificado.");
    }
}
