package com.vending.back.machine.app.rest;

import com.vending.back.machine.app.security.UserDetailsUtil;
import com.vending.back.machine.domain.User;
import com.vending.back.machine.domain.UserAuthForPassword;
import com.vending.back.machine.domain.UserInfo;
import com.vending.back.machine.domain.UserInfoWithRoles;
import com.vending.back.machine.domain.oauth.VbmUser;
import com.vending.back.machine.error.CodeErrorException;
import com.vending.back.machine.helper.*;
import com.vending.back.machine.helper.mail.Templates;
import com.vending.back.machine.helper.translate.ErrorMsg;
import com.vending.back.machine.mapper.UserMapper;
import com.vending.back.machine.model.CreateUserRequest;
import com.vending.back.machine.model.RemindPasswordRequest;
import com.vending.back.machine.model.ResetPasswordRequest;
import com.vending.back.machine.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * vyemialyanchyk on 2/8/2017.
 */
@Slf4j
@RestController
@RequestMapping(value = "/rest", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class UserAccountService extends BaseServiceVbm {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateFormats.DATE_TIME_PATTERN);
    private static final String PARAM_SPLITTER = "##";
    private static final int PASSWORD_RESET_TOKEN_VALID_HOURS = 24;
    private static final int VERIFY_EMAIL_TOKEN_VALID_HOURS = 7 * 24;
    private static final int LOGIN_TOKEN_VALID_HOURS = 4 * 7 * 24;
    private static final String JSON_EMAIL = "email";
    private static final String PARAM_CONTACT = "contact";
    private static final String PARAM_ACTION_BTN_LINK = "actionBtnLink";
    //
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_BEARER = "Bearer";
    //
    private static final String REGEX_NAME = "[A-Za-z]+([A-Za-z\\-'\\.\\s]*)";
    //
    private static final String PARAM_VERIFICATION_EMAIL_LINK = "viewVerificationEmailLink";

    // see 'oauth_client_details' table for more details
    public static final String RESOURCE_ID_RESTSERVICE = "vbm_restservice";
    public static final String CLIENT_ID_VBM_APP = "vbm_app";
    //
    public static final String JSON_TOKEN = "token";
    //
    public static final String AUTHORITY_ROLE_CLIENT_APP = "ROLE_CLIENT_APP";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String VALUE_NO_STORE = "no-store";
    public static final String VALUE_NO_CACHE = "no-cache";


    @Autowired
    @Qualifier("vbmUserDetailsService")
    UserDetailsService userDetailsService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailSenderHelper mailService;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    private ConsumerTokenServices tokenServices;
    @Autowired
    private UserInfoHelper userInfoHelper;

    @Value("${mail.password.reset.url}")
    private String passwordResetServerUrl;

    @Value("${mail.email.verify.url}")
    private String verifyEmailUrl;

    @RequestMapping("/public/password/remind/")
    @ResponseBody
    @Transactional
    public void remindPassword(@RequestBody RemindPasswordRequest remindPasswordRequest) throws UnsupportedEncodingException {
        log.info("remindPassword: " + infoStr(remindPasswordRequest));
        final String email = remindPasswordRequest.getEmail();

        ValidationUtil.checkFieldRequired(ValidationUtil.Field.EMAIL, email);
        final UserAuthForPassword userAuthForPassword = userMapper.getUserAuthForPasswordByEmail(email);
        if (userAuthForPassword != null) {
            if (LocalDateTime.now().minusHours(1).isBefore(userAuthForPassword.getUpdated())) {
                log.info("Your password was updated one hour ago: " + email);
                throw new CodeErrorException(CodeErrorException.ErrorCode.SYSTEM_ERROR, ErrorMsg.YOUR_PASSWORD_WAS_UPDATED_ONE_HOUR_AGO.get());
            }
            final String passwordResetTokenBase64 = generateValidToken();
            final int updated = userMapper.setUserPasswordResetToken(userAuthForPassword.getId(), passwordResetTokenBase64);
            if (updated == 1) {
                notifyUserWithPasswordResetData(userAuthForPassword, passwordResetTokenBase64);
            }
        } else {
            log.info("Your email address is not recognized by our system: " + email);
            throw new CodeErrorException(CodeErrorException.ErrorCode.SYSTEM_ERROR, ErrorMsg.YOUR_EMAIL_ADDRESS_IS_NOT_RECOGNIZED.get());
        }
    }

    public void notifyUserWithPasswordResetData(UserInfoWithRoles userInfo, String passwordResetTokenBase64) throws UnsupportedEncodingException {
        final String emailBase64 = Base64.getEncoder().encodeToString(userInfo.getEmail().getBytes(UTF_8));
        Map<String, Object> emailParams = EmailParams.getCommonParameters(userInfo, userMapper.getUserProfile(userInfo.getId()));
        // emailBase64 used just to display email on UI
        emailParams.put(PARAM_ACTION_BTN_LINK, passwordResetServerUrl + "/" + passwordResetTokenBase64 + "/" + emailBase64);
        mailService.sendFromTemplate(Templates.RESET_PASSWORD, userInfo.getEmail(), emailParams);
    }

    private static String generateValidToken() {
        final String passwordResetToken = LocalDateTime.now().format(DATE_TIME_FORMATTER) + PARAM_SPLITTER + UUID.randomUUID();
        final String passwordResetTokenBase64 = Base64.getEncoder().encodeToString(passwordResetToken.getBytes(UTF_8));
        return passwordResetTokenBase64;
    }

    private static boolean validateToken(final String token, final int tokenValidHours, final String email) {
        boolean valid = token != null && token.trim().length() > 0;
        try {
            if (valid) {
                String decoded = new String(Base64.getDecoder().decode(token), UTF_8.name());
                LocalDateTime time = LocalDateTime.parse(decoded.split(PARAM_SPLITTER)[0], DATE_TIME_FORMATTER);
                LocalDateTime validTill = time.plus(tokenValidHours, ChronoUnit.HOURS);
                valid = LocalDateTime.now().isBefore(validTill);
            }
        } catch (Exception ex) {
            valid = false;
            log.error("Invalid token {} for user email {}: " + ex.getMessage(), token, email, ex);
        }
        return valid;
    }

    @RequestMapping("/logout")
    @ResponseBody
    @Transactional
    public void logout(@AuthenticationPrincipal VbmUser activeUser, HttpServletRequest request) {
        final Long userId = getUserId(activeUser);
        log.info("logout: " + infoStr(userId));
        //
        final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authHeader != null) {
            final String tokenValue = authHeader.replace(HEADER_BEARER, "").trim();
            tokenServices.revokeToken(tokenValue);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET, consumes = ALL_VALUE)
    @ResponseBody
    public UserInfo provideUserInfo(@AuthenticationPrincipal VbmUser activeUser) {
        log.info("provideUserInfo: " + infoStr(getUserId(activeUser)));
        return getUserInfo(activeUser);
    }

    @RequestMapping(value = "/get-login-token", method = RequestMethod.GET, consumes = ALL_VALUE, produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getUserLoginToken(@AuthenticationPrincipal VbmUser activeUser) {
        final Long userId = getUserId(activeUser);
        log.info("getUserLoginToken: " + infoStr(userId));
        final String loginTokenBase64 = generateValidToken();
        final int updated = userMapper.createUserLoginToken(userId, loginTokenBase64);
        if (updated == 1) {
            return new ResponseEntity<String>(loginTokenBase64, HttpStatus.OK);
        }
        return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/public/create-account")
    @ResponseBody
    @Transactional
    public ResponseEntity<OAuth2AccessToken> createAccount(@RequestBody CreateUserRequest createUserRequest, HttpServletRequest request) {
        log.info("createAccount: " + infoStr(createUserRequest, request));
        ValidationUtil.checkRequired(ValidationUtil.Field.CREATE_USER_INFO, createUserRequest);

        createUserRequest.namesNullToEmpty();
        final String email = createUserRequest.getEmail().toLowerCase();
        ValidationUtil.checkFieldRequired(ValidationUtil.Field.EMAIL, email);
        ValidationUtil.checkRequired(ValidationUtil.Field.PASSWORD, createUserRequest.getPassword());

        userInfoHelper.validateEmail(email, null);

        UserInfoHelper.preProcessFields(createUserRequest);
        createUserRequest.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()));

        final String verifyEmailToken = generateValidToken();
        userMapper.createUser(createUserRequest);
        userMapper.createUserSensitive(createUserRequest, verifyEmailToken);

        final Long userId = createUserRequest.getId();
        sendEmailVerificationEmail(email, userId, verifyEmailToken);

        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(Long.toString(userId), 0, TokensForLogin.UseId);
        return getResponse(tokenLoginDetails.accessToken);
    }

    private void validateName(UserInfo userInfo) {
        ValidationUtil.checkFieldRequired(ValidationUtil.Field.FIRST_NAME, userInfo.getFirstName());
        ValidationUtil.checkFieldRequired(ValidationUtil.Field.LAST_NAME, userInfo.getLastName());
        ValidationUtil.checkRegExp(ValidationUtil.Field.FIRST_NAME.name(), userInfo.getFirstName(), REGEX_NAME);
        ValidationUtil.checkRegExp(ValidationUtil.Field.LAST_NAME.name(), userInfo.getFirstName(), REGEX_NAME);
    }

    private void sendEmailVerificationEmail(String email, Long userId, String verifyEmailToken){
        final Map<String, Object> emailParams = EmailParams.getCommonParameters(userMapper.getUserInfoById(userId));
        emailParams.put(PARAM_VERIFICATION_EMAIL_LINK, verifyEmailUrl + "/" + verifyEmailToken);
        mailService.sendFromTemplate(Templates.VERIFICATION_EMAIL, email, emailParams);
    }

    @RequestMapping("/account/name")
    @ResponseBody
    @Transactional
    public UserInfo saveName(@AuthenticationPrincipal VbmUser activeUser, @RequestBody UserInfo userInfo) {
        log.info("saveName: " + infoStr(getUserId(activeUser), userInfo));
        // Validate input
        ValidationUtil.checkRequired(ValidationUtil.Field.USER_INFO, userInfo);
        validateName(userInfo);
        Long userId = getUserId(activeUser);

        UserInfoHelper.preProcessFields(userInfo);

        userMapper.updateUserName(userId, userInfo);

        return userInfo;
    }

    @RequestMapping("/account/email")
    @ResponseBody
    @Transactional
    public UserInfo saveEmail(@AuthenticationPrincipal VbmUser activeUser, @RequestBody UserInfo userInfo) {
        log.info("saveEmail: " + infoStr(getUserId(activeUser), userInfo));
        ValidationUtil.checkRequired(ValidationUtil.Field.USER_INFO, userInfo);
        ValidationUtil.checkFieldRequired(ValidationUtil.Field.EMAIL, userInfo.getEmail());
        final Long userId = getUserId(activeUser);

        String email = userInfo.getEmail();
        userInfoHelper.validateEmail(email, userId);

        UserInfoHelper.preProcessFields(userInfo);
        userMapper.updateUserEmail(userId, email);
        notifyUserEmailChange(userMapper.getUserInfoById(userId));
        return userInfo;
    }

    private void notifyUserEmailChange(UserInfo userInfo ){
        final String receiverEmail = userInfo.getEmail();
        final Map<String, Object> emailParams = EmailParams.getCommonParameters(userInfo, userMapper.getUserProfile(userInfo.getId()));
        mailService.sendFromTemplate(Templates.CHANGE_EMAIL, receiverEmail, emailParams);
    }

    @RequestMapping("/public/password-reset")
    @ResponseBody
    @Transactional
    public ResponseEntity<OAuth2AccessToken> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("resetPassword: " + infoStr(resetPasswordRequest));
        final String email = resetPasswordRequest.getEmail();
        final String passwordResetToken = resetPasswordRequest.getPasswordResetToken();
        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(passwordResetToken, PASSWORD_RESET_TOKEN_VALID_HOURS,
                TokensForLogin.PasswordResetToken);
        final User user = tokenLoginDetails.user;
        ResponseEntity<OAuth2AccessToken> res = null;
        if (user != null) {
            final String passwordHash = passwordEncoder.encode(resetPasswordRequest.getPassword());
            userMapper.setUserPasswordResetTokenAndPassword(user.getId(), null, passwordHash);
            if (tokenLoginDetails.accessToken != null) {
                res = getResponse(tokenLoginDetails.accessToken);
            }
        }
        return res;
    }

    @RequestMapping("/public/verify-email")
    @ResponseBody
    @Transactional
    public ResponseEntity<OAuth2AccessToken> postVerifyEmail(@RequestBody Map<String, String> jsonMapped) {
        log.info("postVerifyEmail: " + infoStr(jsonMapped));
        final String verifyEmailToken = jsonMapped.get(JSON_TOKEN);
        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(verifyEmailToken, VERIFY_EMAIL_TOKEN_VALID_HOURS,
                TokensForLogin.VerifyEmailToken);
        setUserEmailVerified(tokenLoginDetails.user);
        return getResponse(tokenLoginDetails.accessToken);
    }

    @RequestMapping(value = "/public/verify-email/{verifyEmailToken}", method = RequestMethod.GET, consumes = ALL_VALUE)
    @Transactional
    @ResponseBody
    public ResponseEntity<OAuth2AccessToken> getVerifyEmail(@PathVariable String verifyEmailToken) {
        log.info("getVerifyEmail: " + infoStr(verifyEmailToken));
        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(verifyEmailToken, VERIFY_EMAIL_TOKEN_VALID_HOURS,
                TokensForLogin.VerifyEmailToken);
        setUserEmailVerified(tokenLoginDetails.user);
        return getResponse(tokenLoginDetails.accessToken);
    }

    private void setUserEmailVerified(final User user) {
        final Long userId = user.getId();
        if (userId != null) {
            userMapper.setVerifyEmail(userId, true);
            userMapper.setVerifyEmailToken(userId, null);
        }
    }

    @RequestMapping("/public/token-login")
    @ResponseBody
    @Transactional
    public ResponseEntity<OAuth2AccessToken> postTokenLogin(@RequestBody Map<String, String> jsonMapped) {
        log.info("postTokenLogin: " + infoStr(jsonMapped));
        final String loginToken = jsonMapped.get(JSON_TOKEN);
        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(loginToken, LOGIN_TOKEN_VALID_HOURS,
                TokensForLogin.LoginToken);
        return getResponse(tokenLoginDetails.accessToken);
    }

    @RequestMapping(value = "/public/token-login/{loginToken}", method = RequestMethod.GET, consumes = ALL_VALUE)
    @ResponseBody
    public ResponseEntity<OAuth2AccessToken> getTokenLogin(@PathVariable String loginToken) {
        log.info("getTokenLogin: " + infoStr(loginToken));
        final TokenLoginDetails tokenLoginDetails = tokenLoginIntern(loginToken, LOGIN_TOKEN_VALID_HOURS,
                TokensForLogin.LoginToken);
        return getResponse(tokenLoginDetails.accessToken);
    }

    class TokenLoginDetails {
        public User user = null;
        public OAuth2AccessToken accessToken = null;
    }

    @Transactional
    public TokenLoginDetails tokenLoginIntern(final String token, final int tokenValidHours, TokensForLogin tokenForLogin) {
        log.info("tokenLogin: " + infoStr(token));
        ValidationUtil.checkFieldRequired(ValidationUtil.Field.TOKEN, token);
        final TokenLoginDetails res = new TokenLoginDetails();
        final User user = res.user = (TokensForLogin.LoginToken == tokenForLogin) ? userMapper.getUserAuthByLoginToken(token) :
                (TokensForLogin.VerifyEmailToken == tokenForLogin) ? userMapper.getUserAuthByVerifyEmailToken(token) :
                (TokensForLogin.PasswordResetToken == tokenForLogin) ? userMapper.getUserAuthByPasswordResetToken(token) :
                (TokensForLogin.UseEmail == tokenForLogin) ? userMapper.getUserAuthByEmail(token) :
                (TokensForLogin.UseId == tokenForLogin) ? userMapper.getUserAuthById(Long.valueOf(token)) : null;
        if (user == null) {
            log.info("Invalid token: " + infoStr(token));
            throw new CodeErrorException(CodeErrorException.ErrorCode.NOT_FOUND, ErrorMsg.INVALID_TOKEN.get());
        }
        if (TokensForLogin.UseEmail != tokenForLogin && TokensForLogin.UseId != tokenForLogin) {
            if (!validateToken(token, tokenValidHours, user.getEmail())) {
                log.info("Expired token: " + infoStr(token));
                throw new CodeErrorException(CodeErrorException.ErrorCode.NOT_FOUND, ErrorMsg.EXPIRED_TOKEN.get());
            }
        }
        final UserDetails userDetails = UserDetailsUtil.createUserByUsername(user);
        if (userDetails == null) {
            log.info("Invalid user details: " + infoStr(token) + ", " + user.getEmail());
            throw new CodeErrorException(CodeErrorException.ErrorCode.NOT_FOUND, ErrorMsg.INVALID_USER_DETAILS.get());
        }
        //
        final List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(AUTHORITY_ROLE_CLIENT_APP);
        final UsernamePasswordAuthenticationToken userAuth =
                new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        //
        final TreeMap<String, String> map = new TreeMap<>();
        final OAuth2Request storedOAuth2Request = new OAuth2Request(map, CLIENT_ID_VBM_APP, authorities, true,
                Collections.singleton(CLIENT_ID_VBM_APP), Collections.singleton(RESOURCE_ID_RESTSERVICE), null, null, null);
        final OAuth2Authentication oauth2Authentication = new OAuth2Authentication(storedOAuth2Request, userAuth);
        res.accessToken = ((AuthorizationServerTokenServices)tokenServices).createAccessToken(oauth2Authentication);
        //
        return res;
    }

    private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_CACHE_CONTROL, VALUE_NO_STORE);
        headers.set(HEADER_PRAGMA, VALUE_NO_CACHE);
        return new ResponseEntity<OAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
    }
}
