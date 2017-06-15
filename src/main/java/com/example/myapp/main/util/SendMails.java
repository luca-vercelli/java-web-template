package com.example.myapp.main.util;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @see https://stackoverflow.com/questions/26548059/sending-email-with-ssl-using-javax-mail
 * and https://webapps4newbies.blogspot.it/2015/03/configuring-javamail-sessions-in.html
 *
 */
public class SendMails {
	
    @Resource(name = "mail/mainMailSession")
    private Session session;
    
	public void simpleSend() {

	    try {

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress("frommail@example.com"));
	        message.setRecipients(Message.RecipientType.TO,
	                InternetAddress.parse("tomail@example.com"));
	        message.setSubject("Testing Subject");
	        message.setText("Test Mail");

	        Transport.send(message);

	        System.out.println("Done");

	    } catch (MessagingException e) {
	        throw new RuntimeException(e);
	    }
	}
}
