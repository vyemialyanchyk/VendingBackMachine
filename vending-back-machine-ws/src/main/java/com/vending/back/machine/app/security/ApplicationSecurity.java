package com.vending.back.machine.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * vyemialyanchyk on 12/21/2016.
 */
@Configuration
@EnableResourceServer
//@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
//security.oauth2.resource.filter-order: 3
public class ApplicationSecurity extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "vbm_restservice";

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //http.csrf();
        http.authorizeRequests()
                //public access to swagger
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/**").permitAll()

                //public access to test websockets
                .antMatchers("/ws-chanel").permitAll()
                .antMatchers("/ws-chanel/**").permitAll()
                //public access to REST API
                .antMatchers("/rest/public/**").permitAll()

                //public access to fake login page cause use OAuth
                //.antMatchers("/rest/login").permitAll()

                .antMatchers("/rest/admin/**").access("hasRole('MANAGER') and #oauth2.clientHasRole('ROLE_MANAGE_APP')")
                .antMatchers("/rest/account/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/logout").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/user/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/group/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/notification/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/get-login-token/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/msg/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                .antMatchers("/rest/profile/**").access("#oauth2.clientHasRole('ROLE_MANAGE_APP') or #oauth2.clientHasRole('ROLE_CLIENT_APP')")
                //managers and admins
                .antMatchers("/rest/payment/**").access("hasRole('ADMIN') and #oauth2.clientHasRole('ROLE_MANAGE_APP')")
                .antMatchers("/rest/manager/**").access("(hasRole('ADMIN') or hasRole('MANAGER')) and #oauth2.clientHasRole('ROLE_MANAGE_APP')")
                .antMatchers("/rest/roxy/**").access("(hasRole('PROMOTIONS') or hasRole('ADMIN')) and #oauth2.clientHasRole('ROLE_MANAGE_APP')")
                //admin only
                .antMatchers("/rest/admin/**").access("hasRole('ADMIN') and #oauth2.clientHasRole('ROLE_MANAGE_APP')")


                //clients (applicants) only
                .anyRequest().access("#oauth2.clientHasRole('ROLE_CLIENT_APP')");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).tokenStore(tokenStore);
    }
}
