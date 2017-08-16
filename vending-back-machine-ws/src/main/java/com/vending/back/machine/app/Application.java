package com.vending.back.machine.app;

import com.vending.back.machine.dao.PersistenceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * vyemialyanchyk on 12/19/2016.
 */
@SpringBootApplication
@EnableSwagger2
@Import({ PersistenceConfig.class})
@EnableScheduling
@Slf4j
public class Application extends SpringBootServletInitializer {

    public static final String ROLE_CLIENT_APP = "ROLE_CLIENT_APP";
    public static final String ROLE_MANAGER_APP = "ROLE_MANAGER_APP";
    public static final String SCOPE_GLOBAL = "global";
    public static final String DESCRIPTION_GLOBAL_SCOPE = "globalScope";
    public static final String PACKAGE_VBM_APP_REST = "com.vending.back.machine.app.rest";
    private static Class<Application> applicationClass = Application.class;

    @PostConstruct
    public void init() {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    @Bean(name = "restTemplate")
    public RestTemplate getRestTemplate() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.disableRedirectHandling();

        CloseableHttpClient httpClient = httpClientBuilder.build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> originalConverters = restTemplate.getMessageConverters();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "css"),
                MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        originalConverters.add(jsonConverter);

        return restTemplate;
    }

    public static void main(String[] args) {
//        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//	    // generate pass hash for test admin & managers
//	    String pass01 = passwordEncoder.encode("Vbm12");
//	    // generate pass hash for test users
//	    String pass02 = passwordEncoder.encode("12");
//	    // generate pass hash for FE side
//	    String pass03 = passwordEncoder.encode("vbm_mgmt_secret_key");
	    //
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        //
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        log.info("BEGIN: List of available beans: ");
        for (String beanName : beanNames) {
            log.info(beanName);
        }
        log.info("END: List of available beans.");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(PACKAGE_VBM_APP_REST))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDateTime.class, String.class)
                .securityContexts(newArrayList(securityContext()));
    }

    private springfox.documentation.spi.service.contexts.SecurityContext securityContext() {
        return springfox.documentation.spi.service.contexts.SecurityContext.builder()
                .securityReferences(securityReferences())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope(SCOPE_GLOBAL, DESCRIPTION_GLOBAL_SCOPE)};
        // Currently we have 2 roles - user and client
        return newArrayList(
                new SecurityReference(ROLE_CLIENT_APP, authorizationScopes),
                new SecurityReference(ROLE_MANAGER_APP, authorizationScopes)
        );
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    /**
     * This first section of the configuration just makes sure that Spring Security picks
     * up the UserDetailsService that we create in security configs.
     */
    @Configuration
    @EnableWebSecurity
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("vbmUserDetailsService")
        UserDetailsService userDetailsService;

        @Autowired
        protected void registerAuthentication(final AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
        }
    }

}
