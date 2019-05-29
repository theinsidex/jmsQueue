/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "SendMessage", urlPatterns = {"/SendMessage"})
@MessageDriven(name="SendMessage",
 mappedName="Destination2"
)
public class SendMessage extends HttpServlet implements MessageListener{
    @Resource(mappedName="ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName="Destination1")
    private Queue destination1;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String strMessage = request.getParameter("message");
        if (strMessage!=null && !"".equals(strMessage.trim())) {
            try {
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(destination1);
                
                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(strMessage);
                
                messageProducer.send(textMessage);
                System.out.println("Message send!!-->"+strMessage+"<--");
                messageProducer.close();
                connection.close();                
            }
            catch (JMSException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
            }            
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SendMessage</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1> Sender messages to queue </h1>");
            
            // The following code adds the form to the web page
            out.println("<form method=\"post\" >");            
            out.println("Message: <input type='text' name='message'><br/>");
            out.println("</br>");                                    
            out.println("<input type='submit'><br/>");
            out.println("</form>");                        
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
      
        try {
            System.out.println("Message by Consumer to Sender-->"+textMessage.getText());
        } catch (JMSException ex) {
            Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
