package web;

import java.io.PrintWriter;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@MessageDriven(name="Consumer",
 mappedName="Destination1"
)
public class Consumer implements MessageListener{
    @Resource(mappedName="ConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName="Destination2")
    private Queue destination2;
    
    @Override
    public void onMessage(Message message) {
    TextMessage textMessage=(TextMessage) message;
        try {
            System.out.println("Message from Consumer-->"+textMessage.getText());
             Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(destination2);
                
                TextMessage textMessageSend = session.createTextMessage();
                textMessageSend.setText("Hello,"+ textMessage.getText());
                
                messageProducer.send(textMessageSend);
        } catch (JMSException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}