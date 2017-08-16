package com.vending.back.machine.app.rest;

import com.vending.back.machine.app.error.ErrorResponse;
import com.vending.back.machine.domain.UserInfoWithRoles;
import com.vending.back.machine.domain.oauth.VbmUser;
import com.vending.back.machine.error.CodeErrorException;
import com.vending.back.machine.error.InputValidationException;
import com.vending.back.machine.error.IntegrationException;
import com.vending.back.machine.helper.MailSenderHelper;
import com.vending.back.machine.helper.mail.Templates;
import com.vending.back.machine.util.EnvironmentUtil;
import com.vending.back.machine.util.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * vyemialyanchyk on 2/8/2017.
 */
@Slf4j
@Component
public class BaseServiceVbm {

    @Autowired
    private Environment environment;

    @Autowired
    private MailSenderHelper mailService;

    @Value("${mail.system.failure.notify}")
    private String failuresTo;

    @ExceptionHandler(CodeErrorException.class)
    public ResponseEntity<ErrorResponse> handleApplicationError(CodeErrorException ex) {
        log.warn(ex.getMessage());

        ErrorResponse responseStatus = error(ex.getCode(), ex.getMessage(), ex);
        if (ex.getCode().equals(CodeErrorException.ErrorCode.SYSTEM_ERROR.name())) {
            log.error("CodeError exception, srvc0: " + ex.getMessage(), ex);
            return new ResponseEntity<>(responseStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            log.info("CodeError exception, srvc1: " + ex.getMessage(), ex);
            return new ResponseEntity<>(responseStatus, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationError(InputValidationException ex) {
        log.debug("Validation exception: " + ex.getMessage(), ex);
        ErrorResponse responseStatus = error(ex.getField(), ex.getMessage(), ex);
        return new ResponseEntity<>(responseStatus, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationException(IntegrationException ex) {
        log.error("Integration exception: " + ex.getMessage(), ex);

        ErrorResponse responseStatus = error(ex.getSystem().name(), ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ResponseEntity<ErrorResponse> response = new ResponseEntity<>(responseStatus, status);

        Writer stackTrace = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stackTrace);
        ex.printStackTrace(printWriter);

        Map<String, Object> emailParams = new HashMap<>();
        emailParams.put("message", ex.getMessage());
        emailParams.put("time", LocalDateTime.now());
        emailParams.put("stackTrace", stackTrace.toString());
        try {
            mailService.sendFromTemplate(Templates.INTEGRATION_ERROR, failuresTo, emailParams);
        } catch (Exception ex02) {
            log.error("Error sending email message: " + ex02.getMessage(), ex02);
        }
        return response;
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(NullPointerException ex) {
        log.error("NPE exception: " + ex.getMessage(), ex);
        ErrorResponse responseStatus = error(CodeErrorException.ErrorCode.NOT_FOUND.name(), "Requested resource not found", ex);
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(responseStatus, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(RuntimeException ex) {
        log.error("Runtime exception: " + ex.getMessage(), ex);

        ErrorResponse responseStatus = error(CodeErrorException.ErrorCode.SYSTEM_ERROR.name(), ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(responseStatus, status);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDBException(RuntimeException ex) {
        log.error("DB operation failed: " + ex.getMessage(), ex);
        ErrorResponse responseStatus = error(CodeErrorException.ErrorCode.SYSTEM_ERROR.name(), "Error accessing data base", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(responseStatus, status);
    }

    private ErrorResponse error(String code, String message, Exception ex) {
        boolean advancedErrorInfo = ex != null && !EnvironmentUtil.isProd(environment);
        return new ErrorResponse(
                code,
                message,
                advancedErrorInfo ? ExceptionUtils.getStackTrace(ex) : null,
                advancedErrorInfo ? ex.toString() : null
        );
    }

    protected Long getUserId(VbmUser activeUser) {
        return activeUser != null ? activeUser.getUserId() : null;
    }

    protected UserInfoWithRoles getUserInfo(VbmUser activeUser) {
        UserInfoWithRoles info = new UserInfoWithRoles(activeUser.getRolesSet());
        info.setId(activeUser.getUserId());
        info.setEmail(activeUser.getUsername());
        info.setFirstName(activeUser.getFirstName());
        info.setLastName(activeUser.getLastName());
        return info;
    }

    protected String infoStr(Object... params) {
        return Log.infoStr(params);
    }
}
