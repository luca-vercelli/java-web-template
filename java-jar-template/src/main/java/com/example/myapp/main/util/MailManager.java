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
	Logger LOG;

	@Resource(name = "mail/mainMailSession")
	private Session session;


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
	 * Prepare a Message. You must complete it, then send it using
	 * Transport.send().
	 */
	public Message prepareEmptyMessage() {
		return new MimeMessage(session);
	}

	/**
	 * Send a simple-text email.
	 * 
	 * @param recipients
	 *            comma-separated emails.
	 * @param subject
	 * @param text
	 * @throws MessagingException
	 */
	public void sendSimpleTextMail(String recipients, String subject, String text) throws MessagingException {
		try {
			SimpleMessage message = new SimpleMessage(session, FROM, recipients, subject);
			message.setText(text);
			message.send();

			logger.info("Sent email to " + recipients);

		} catch (MessagingException e) {
			logger.error("Error sending email", e);
			throw e;
		}
	}

	/**
	 * Send a multipart text/HTML email.
	 * 
	 * @param recipients
	 *            comma-separated emails.
	 * @param subject
	 * @param text
	 *            plain-text email content
	 * @param HTML
	 *            HTML email content
	 * @param attachments
	 *            file to send as attachments
	 * @throws MessagingException
	 */
	public void sendSimpleHTMLMail(String recipients, String subject, String text, String HTML, File... attachments)
			throws MessagingException {
		try {
			SimpleMessage message = new SimpleMessage(session, FROM, recipients, subject);
			message.setText(text);
			message.setHtmlText(HTML);
			message.addAttachments(attachments);
			message.send();

			logger.info("Sent email to " + recipients);

		} catch (MessagingException e) {
			logger.error("Error sending email", e);
			throw e;
		}
	}

	/**
	 * Send a test email to recipient@example.com.
	 * 
	 * @throws MessagingException
	 */
	public void testMail() throws MessagingException {

		sendSimpleTextMail("recipient@example.com", "Testing Subject", "Test Mail");

	}

	/**
	 * This class automatically decides if the message needs to be multipart or
	 * not.
	 * 
	 * Please use either the setText() or setHtmlText() methods, add attachments
	 * as required, then send with send().
	 * 
	 * If you need some more Transport options, you can call getMessage(), then send it by yourself. 
	 * 
	 * @author u0i8226
	 *
	 */
	public static class SimpleMessage {
		private String text;
		private String htmlText;
		private List<File> attachments = new ArrayList<File>();
		private MimeMessage message;
		private Multipart multipart;

		public SimpleMessage(Session session, String from, String recipients, String subject)
				throws MessagingException {

			message = new MimeMessage(session);
			message.setFrom(from);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			message.setSubject(subject);
		}

		public SimpleMessage(Session session, String from, String recipients, String recipientsCC, String recipientsBCC,
				String subject) throws MessagingException {

			this(session, from, recipients, subject);
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(recipientsCC));
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipientsBCC));
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getHtmlText() {
			return htmlText;
		}

		public void setHtmlText(String htmlText) {
			this.htmlText = htmlText;
		}

		public List<File> getAttachments() {
			return attachments;
		}

		public void addAttachment(File attachment) {
			this.attachments.add(attachment);
		}

		public void addAttachments(List<File> attachments) {
			this.attachments.addAll(attachments);
		}

		public void addAttachments(File[] attachments) {
			this.attachments.addAll(Arrays.asList(attachments));
		}

		/**
		 * Fill the internal object MimeMessage
		 * 
		 * @return
		 * @throws MessagingException
		 */
		public MimeMessage getMessage() throws MessagingException {

			if (htmlText == null) {
				// simple text mail
				message.setText(text);
			} else if (text == null) {
				// HTML only mail
				message.setContent(htmlText, "text/html");
			} else {
				// both simple text and HTML, so this mail is multipart
				final MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(text, "text/plain");

				final MimeBodyPart htmlPart = new MimeBodyPart();
				htmlPart.setContent(htmlText, "text/html");

				multipart = new MimeMultipart();
				multipart.addBodyPart(textPart);
				multipart.addBodyPart(htmlPart);
			}

			if (!attachments.isEmpty() && multipart == null) {
				multipart = new MimeMultipart();
			}

			for (File file : attachments) {
				multipart.addBodyPart(file2MimeBodyPart(file));
			}

			if (multipart != null) {
				message.setContent(multipart);
			}

			return message;
		}

		/**
		 * Create and send the email
		 * 
		 * @throws MessagingException
		 */
		public void send() throws MessagingException {
			Transport.send(getMessage());
		}

		/**
		 * Create a new MimeBodyPart containing given file as attachment
		 * 
		 * @param file
		 * @return
		 * @throws MessagingException
		 */
		private BodyPart file2MimeBodyPart(File file) throws MessagingException {
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file.getName());
			return messageBodyPart;
		}
	}
}
