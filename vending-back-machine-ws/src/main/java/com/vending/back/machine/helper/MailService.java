package com.vending.back.machine.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * vyemialyanchyk on 12.1.17.
 */
@Slf4j
@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ResourcePatternResolver resourceResolver;

	public void sendWithoutImages(String from, String to, String subject, String templatePath, Map<String, ?> templateParams) {
		StringWriter w = new StringWriter();
		Velocity.mergeTemplate(templatePath, StandardCharsets.UTF_8.name(), new VelocityContext(templateParams), w);
		send(from, to, subject, w.toString(), new Resource[0]);
	}

	private void send(String from, String to, String subject, String htmlText, Resource[] embeddedImages) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
			message.setFrom(from);
			message.setTo(InternetAddress.parse(to));
			message.setSubject(subject);

			message.setText(htmlText, true);

			for (Resource imageResource : embeddedImages) {
				String fileName = imageResource.getFilename();
				String name = fileName.substring(0, fileName.lastIndexOf('.'));
				message.addInline(name, imageResource);
			}

			mailSender.send(message.getMimeMessage());
		} catch (MessagingException ex) {
			log.error("Error sending email: " + ex.getMessage(), ex);
			throw new RuntimeException("Error sending email: " + ex.getMessage(), ex);
		}

	}
}
