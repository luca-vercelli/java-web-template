package com.example.myapp.main.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
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
 * required, then send with Transport.send(message.getMessage()).
 * 
 * 
 * @author LV
 *
 */
public class SimpleMessage {

	private String subject;
	private String from;
	private InternetAddress[] recipients;
	private InternetAddress[] recipientsCC;
	private InternetAddress[] recipientsBCC;
	private String text;
	private String htmlText;
	private List<MimeBodyPart> attachments = new ArrayList<>();
	private Map<String, Object> parameters = new HashMap<>();

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
		this.htmlText = htmlText;
		return this;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Set a map of parameters that will be replaced inside the mail body and
	 * subject.
	 * 
	 * For example, if the map contains an entry "NAME" = "Goofy", alle
	 * occurrences of string "{NAME}" will be replaced with "Goofy" before send.
	 * 
	 * @param parameters
	 */
	public SimpleMessage setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	public SimpleMessage addParameters(String key, Object value) {
		this.parameters.put(key, value);
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public SimpleMessage setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getFrom() {
		return from;
	}

	public SimpleMessage setFrom(String from) {
		this.from = from;
		return this;
	}

	public InternetAddress[] getRecipients() {
		return recipients;
	}

	public SimpleMessage setRecipients(InternetAddress[] recipients) {
		this.recipients = recipients;
		return this;
	}

	public SimpleMessage setRecipients(String recipients) throws AddressException {
		if (recipients == null) {
			this.recipients = null;
		} else {
			this.recipients = InternetAddress.parse(recipients);
		}
		return this;
	}

	public InternetAddress[] getRecipientsCC() {
		return recipientsCC;
	}

	public SimpleMessage setRecipientsCC(InternetAddress[] recipientsCC) {
		this.recipientsCC = recipientsCC;
		return this;
	}

	public SimpleMessage setRecipientsCC(String recipients) throws AddressException {
		if (recipients == null) {
			this.recipientsCC = null;
		} else {
			this.recipientsCC = InternetAddress.parse(recipients);
		}
		return this;
	}

	public InternetAddress[] getRecipientsBCC() {
		return recipientsBCC;
	}

	public SimpleMessage setRecipientsBCC(InternetAddress[] recipientsBCC) {
		this.recipientsBCC = recipientsBCC;
		return this;
	}

	public SimpleMessage setRecipientsBCC(String recipients) throws AddressException {
		if (recipients == null) {
			this.recipientsBCC = null;
		} else {
			this.recipientsBCC = InternetAddress.parse(recipients);
		}
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
	public MimeMessage getMessage(Session session) throws MessagingException {

		if (subject == null) {
			throw new MessagingException("Missing 'subject' field");
		}
		if (from == null) {
			throw new MessagingException("Missing 'from' field");
		}
		if ((recipients == null || recipients.length == 0) && (recipientsCC == null || recipientsCC.length == 0)
				&& (recipientsBCC == null || recipientsBCC.length == 0)) {
			throw new MessagingException("At least one recipient is required");
		}

		MimeMessage message = new MimeMessage(session);
		message.setSubject(replaceParameters(subject));
		message.setFrom(new InternetAddress(from));
		if (recipients != null)
			message.setRecipients(Message.RecipientType.TO, recipients);
		if (recipientsCC != null)
			message.setRecipients(Message.RecipientType.CC, recipientsCC);
		if (recipientsBCC != null)
			message.setRecipients(Message.RecipientType.BCC, recipientsBCC);

		if (htmlText == null && attachments.isEmpty()) {
			// simple text mail, without attachments
			message.setText(replaceParameters(text));

		} else if (text == null && attachments.isEmpty()) {
			// HTML only mail, without attachments
			message.setContent(replaceParameters(htmlText), "text/html");

		} else {

			// multipart mail
			// either because this is both simple text and HTML,
			// or because there is some attachment

			MimeMultipart multipart = new MimeMultipart();

			// HTML part must be first
			if (htmlText != null) {
				MimeBodyPart htmlPart = new MimeBodyPart();
				htmlPart.setContent(replaceParameters(htmlText), "text/html");
				multipart.addBodyPart(htmlPart);
			}

			// Then, text part
			if (text != null) {
				MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(replaceParameters(text), "text/plain");
				multipart.addBodyPart(textPart);
			}

			// At last, attachments
			for (MimeBodyPart bodyPart : attachments) {
				multipart.addBodyPart(bodyPart);
			}

			message.setContent(multipart);
		}

		return message;
	}

	private String replaceParameters(String text) {
		for (String key : parameters.keySet()) {
			String value = parameters.containsKey(key) && parameters.get(key) != null ? parameters.get(key).toString()
					: "";
			text = text.replace("{" + key + "}", value);
		}
		return text;
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
