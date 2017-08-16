package com.vending.back.machine.helper;

import com.vending.back.machine.helper.mail.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

/**
 * vyemialyanchyk on 12.1.17.
 */
@Slf4j
@Service
public class MailSenderHelper {

	private static final String BUNDLE_SUBJECT_KEY_PREFIX = "subject.";
	private static final String BUNDLE_TEMPLATE_KEY_PREFIX = "template.";

	@Autowired
	private MailService mailService;

	@Value("${mail.senderEmail}")
	private String senderEmail;

	@Value("${mail.dataUploadError.recipientsEmail}")
	private String recipientsEmail;

	public void sendDataUploadProcessEmail(String content) {
		Map<String, Object> emailParams = new TreeMap<String, Object>();
		emailParams.put("content", content);
		try {
			sendFromTemplate("DATA_UPLOAD_PROCESS", recipientsEmail, emailParams);
		} catch (Exception ex) {
			log.error("Send email error: " + ex.getMessage(), ex);
		}
	}

	public void sendFromTemplate(Templates template, String receiversEmails, Map<String, Object> emailParams) {
		sendFromTemplate(template.name(), receiversEmails, emailParams);
	}

	private void sendFromTemplate(String contentTemplateName, String recipients, Map<String, Object> emailParams) {
		final String subject = BUNDLE_SUBJECT_KEY_PREFIX + contentTemplateName;
		final String template = BUNDLE_TEMPLATE_KEY_PREFIX + contentTemplateName;

		mailService.sendWithoutImages(senderEmail, recipients, subject, template, emailParams);
	}
}
