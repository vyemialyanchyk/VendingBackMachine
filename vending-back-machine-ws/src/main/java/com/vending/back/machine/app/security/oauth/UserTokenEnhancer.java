package com.vending.back.machine.app.security.oauth;

import com.vending.back.machine.domain.oauth.VbmUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;
import java.util.TreeMap;

/**
 * vyemialyanchyk on 12/21/2016.
 */
public class UserTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> information = new TreeMap<>(result.getAdditionalInformation());
        Object principal = authentication.getPrincipal();
        if (principal instanceof VbmUserDetails) {
            VbmUserDetails vbmUser = (VbmUserDetails) principal;
            information.put("email", vbmUser.getUsername());
            information.put("user_id", vbmUser.getUserId());
            information.put("first_name", vbmUser.getFirstName());
            information.put("last_name", vbmUser.getLastName());
            information.put("verify_email", vbmUser.getVerifyEmail());
            information.put("roles", vbmUser.getRoles());
        }
        result.setAdditionalInformation(information);
        return result;
    }

}
