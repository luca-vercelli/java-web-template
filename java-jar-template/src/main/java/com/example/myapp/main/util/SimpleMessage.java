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
 * <br>
 * 
 * Usage: call either the setText() or setHtmlText() methods, add attachments as
 * required, then call send().
 * 
 * <br>
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

	// calculated attributes
	private MimeMessage message;
	private Multipart multipart;
	private boolean compiled = false;

	public SimpleMessage(Session session, String from, String recipients, String subject) throws MessagingException {
		this(session, from, recipients, null, null, subject);
	}

	public SimpleMessage(Session session, String from, String recipients, String recipientsCC, String recipientsBCC,
			String subject) throws MessagingException {

		message = new MimeMessage(session);
		message.setSubject(subject);
		message.setFrom(new InternetAddress(from));
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

	/**
	 * Set a simple text for email content.
	 * 
	 * If both Text and HtmlText are set, the client decides what to show.
	 * 
	 * @param text
	 * @return
	 */
	public SimpleMessage setText(String text) {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.text = text;
		return this;
	}

	public String getHtmlText() {
		return htmlText;
	}

	/**
	 * Set a HTML text for email content.
	 * 
	 * If both Text and HtmlText are set, the client decides what to show.
	 * 
	 * @param htmlText
	 *            Something like "&lt;HTML&gt;&lt;BODY&gt; Some text ...
	 *            &lt;/BODY&gt;&lt;/HTML&gt;
	 * @return
	 */
	public SimpleMessage setHtmlText(String htmlText) {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.htmlText = htmlText;
		return this;
	}

	public List<MimeBodyPart> getAttachments() {
		return attachments;
	}

	/**
	 * Add a given file as attachment, guessing its MIME type.
	 * 
	 * @param attachment
	 * @return
	 * @throws MessagingException
	 */
	public SimpleMessage addAttachment(File attachment) throws MessagingException {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.attachments.add(file2MimeBodyPart(attachment));
		return this;
	}

	/**
	 * Add an attachment whose content is a given byte array.
	 * 
	 * @param fileContent
	 * @param mimeType
	 * @param filename
	 * @return
	 * @throws MessagingException
	 */
	public SimpleMessage addAttachment(byte[] fileContent, String mimeType, String filename) throws MessagingException {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.attachments.add(byte2MimeBodyPart(fileContent, mimeType, filename));
		return this;
	}

	/**
	 * Add an attachment whose content is a given InputStream.
	 * 
	 * @param fileContent
	 * @param mimeType
	 * @param filename
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public SimpleMessage addAttachment(InputStream fileContent, String mimeType, String filename)
			throws MessagingException, IOException {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.attachments.add(inputStream2MimeBodyPart(fileContent, mimeType, filename));
		return this;
	}

	/**
	 * Add a MimeBodyPart as attachment, whose whole content is given by an
	 * InputStream.
	 * 
	 * @param mimeBodyPart
	 * @return
	 * @throws MessagingException
	 */
	public SimpleMessage addAttachment(InputStream mimeBodyPart) throws MessagingException {
		if (compiled)
			throw new IllegalStateException("The Message has already been compiled.");
		this.attachments.add(new MimeBodyPart(mimeBodyPart));
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
	 * Compile the message, then return the internal MimeMessage object.
	 * 
	 * When you call this method, the internal object is filled with all
	 * required data, and no more edits are allowed.
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public MimeMessage getMessage() throws MessagingException {

		if (!compiled) {
			if (htmlText == null && attachments.isEmpty()) {
				// simple text mail, without attachments
				message.setText(text);

			} else if (text == null && attachments.isEmpty()) {
				// HTML only mail, without attachments
				message.setContent(htmlText, "text/html");

			} else {

				// multipart mail
				// either because this is both simple text and HTML,
				// or because there is some attachment

				multipart = new MimeMultipart();

				// HTML part must be first
				if (htmlText != null) {
					MimeBodyPart htmlPart = new MimeBodyPart();
					htmlPart.setContent(htmlText, "text/html");
					multipart.addBodyPart(htmlPart);
				}

				// Then, text part
				if (text != null) {
					MimeBodyPart textPart = new MimeBodyPart();
					textPart.setContent(text, "text/plain");
					multipart.addBodyPart(textPart);
				}

				// At last, attachments
				for (MimeBodyPart bodyPart : attachments) {
					multipart.addBodyPart(bodyPart);
				}

				message.setContent(multipart);
			}

			compiled = true;
		}
		return message;
	}

	/**
	 * Return true if the object has not been compiled yet.
	 * 
	 * @return
	 */
	public boolean canBeModified() {
		return !compiled;
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
	private static MimeBodyPart inputStream2MimeBodyPart(InputStream fileContent, String mimeType, String filename)
			throws MessagingException, IOException {
		final MimeBodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new ByteArrayDataSource(fileContent, mimeType);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		return messageBodyPart;
	}
}
