package com.example.myapp.main.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * This class is intended as a convenience wrapper around javax.mail Message /
 * MimeMessage classes.
 * 
 * This class automatically decides if the message needs to be multipart or not.
 * 
 * Please use either the setText() or setHtmlText() methods, add attachments as
 * required, then send with send().
 * 
 * If you need some more Transport options, you can call getMessage(), then send
 * it by yourself.
 * 
 * @author LV
 *
 */
public class SimpleMessage {
	private String text;
	private String htmlText;
	private List<MimeBodyPart> attachments = new ArrayList<>();
	private MimeMessage message;
	private Multipart multipart;
	private boolean completed = false;

	public SimpleMessage(Session session, String from, String recipients, String subject) throws MessagingException {
		this(session, from, recipients, null, null, subject);
	}

	public SimpleMessage(Session session, String from, String recipients, String recipientsCC, String recipientsBCC,
			String subject) throws MessagingException {

		message = new MimeMessage(session);
		message.setSubject(subject);
		message.setFrom(from);
		if (recipients != null)
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
		if (recipientsCC != null)
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(recipientsCC));
		if (recipientsBCC != null)
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipientsBCC));
	}

	public String getText() {
		return text;
	}

	public SimpleMessage setText(String text) {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.text = text;
		return this;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public SimpleMessage setHtmlText(String htmlText) {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.htmlText = htmlText;
		return this;
	}

	public List<MimeBodyPart> getAttachments() {
		return attachments;
	}

	public SimpleMessage addAttachment(File attachment) throws MessagingException {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.attachments.add(file2MimeBodyPart(attachment));
		return this;
	}

	public SimpleMessage addAttachment(byte[] fileContent, String mimeType, String filename) throws MessagingException {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.attachments.add(byte2MimeBodyPart(fileContent, mimeType, filename));
		return this;
	}

	public SimpleMessage addAttachment(InputStream mimeBodyPart) throws MessagingException {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.attachments.add(new MimeBodyPart(mimeBodyPart));
		return this;
	}

	public SimpleMessage addAttachment(InputStream fileContent, String mimeType, String filename)
			throws MessagingException, IOException {
		if (completed)
			throw new IllegalStateException("The Message has already been completed.");
		this.attachments.add(byte2MimeBodyPart(fileContent, mimeType, filename));
		return this;
	}

	public SimpleMessage addAttachments(Iterable<File> attachments) throws MessagingException {
		for (File f : attachments)
			addAttachment(f);
		return this;
	}

	public SimpleMessage addAttachments(File[] attachments) throws MessagingException {
		for (File f : attachments)
			addAttachment(f);
		return this;
	}

	/**
	 * Complete and return the internal MimeMessage object.
	 * 
	 * When you call this method, the internal object is filled with all
	 * required data, and no more edits are allowed.
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public MimeMessage getMessage() throws MessagingException {

		if (!completed) {
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

			for (MimeBodyPart bodyPart : attachments) {
				multipart.addBodyPart(bodyPart);
			}

			if (multipart != null) {
				message.setContent(multipart);
			}
			completed = true;
		}
		return message;
	}

	/**
	 * Create and send the email
	 * 
	 * @throws MessagingException
	 */
	public SimpleMessage send() throws MessagingException {
		Transport.send(getMessage());
		return this;
	}

	/**
	 * Create a new MimeBodyPart containing given file as attachment
	 * 
	 * @param file
	 * @return
	 * @throws MessagingException
	 */
	private static MimeBodyPart file2MimeBodyPart(File file) throws MessagingException {
		final MimeBodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(file.getName());
		return messageBodyPart;
	}

	/**
	 * Create a new MimeBodyPart containing given data as attachment
	 * 
	 * @return
	 * @throws MessagingException
	 */
	private static MimeBodyPart byte2MimeBodyPart(byte[] fileContent, String mimeType, String filename)
			throws MessagingException {
		final MimeBodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new ByteArrayDataSource(fileContent, mimeType);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		return messageBodyPart;
	}

	/**
	 * Create a new MimeBodyPart containing given data as attachment
	 * 
	 * This is different from new MimeBodyPart(data), as here data is the file
	 * content only
	 * 
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static MimeBodyPart byte2MimeBodyPart(InputStream fileContent, String mimeType, String filename)
			throws MessagingException, IOException {
		final MimeBodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new ByteArrayDataSource(fileContent, mimeType);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		return messageBodyPart;
	}
}
