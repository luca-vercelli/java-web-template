package com.example.myapp.main.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.Stateless;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;

/**
 * Send emails using container-managed SMTP provider.
 * 
 * @see https://stackoverflow.com/questions/26548059/sending-email-with-ssl-using-javax-mail
 *      and
 *      https://webapps4newbies.blogspot.it/2015/03/configuring-javamail-sessions-in.html
 *
 */
@Stateless
public class MailManager {

	@Inject
	Logger logger;

	@Resource(name = "mail/mainMailSession")
	private Session session;

	private final static String FROM = "me@example.com";
	
	/**
	 * Alternative way to create current session, with hard-coded parameters
	 * 
	 * @return
	 */
	public Session getSession() {
		if (session == null) {
			Properties props = new Properties();
			props.put("mail.smtp.host", "myhost.example.com");
			props.put("mail.smtp.port", "25");
			props.put("mail.smtp.starttls.enable", "false");
			props.put("mail.smtp.auth", "true");
			session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("xxx", "xxx");
				}
			});
		}
		return session;
	}

	/**
	 * Create a new SimpleMessage with pre-filled data
	 * 
	 * @param from
	 * @param recipients
	 * @param subject
	 * @return
	 * @throws MessagingException
	 */
	public SimpleMessage prepareEmail(String recipients, String subject) throws MessagingException {
		return new SimpleMessage().setFrom(FROM).setRecipients(recipients).setSubject(subject);
	}

	/**
	 * Create a new SimpleMessage with pre-filled data
	 * 
	 * @param from
	 * @param recipients
	 * @param recipientsCC
	 * @param recipientsBCC
	 * @param subject
	 * @return
	 * @throws MessagingException
	 */
	public SimpleMessage prepareEmail(String recipients, String recipientsCC, String recipientsBCC,
			String subject) throws MessagingException {
		return new SimpleMessage().setFrom(FROM).setRecipients(recipients).setRecipientsCC(recipientsCC)
				.setRecipientsBCC(recipientsBCC).setSubject(subject);
	}

	public void send(SimpleMessage msg) throws MessagingException {
		Transport.send(msg.getMessage(getSession()));
	}
}
