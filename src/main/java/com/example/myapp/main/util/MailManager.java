package com.example.myapp.main.util;

import java.io.File;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.NotImplementedException;
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

			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

			LOG.info("Sent email to " + recipients);

		} catch (MessagingException e) {
			LOG.error("Error sending email", e);
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

		throw new NotImplementedException("TODO");
	}

	/**
	 * Send a test email to recipient@example.com.
	 * 
	 * @throws MessagingException
	 */
	public void testMail() throws MessagingException {

		sendSimpleTextMail("recipient@example.com", "Testing Subject", "Test Mail");

	}
}
