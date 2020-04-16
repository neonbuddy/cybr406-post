package com.cybr406.post.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.Validator;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    //Stored base url into application properties, allows for easy config
    @Value("${account.url}")
    private String accountUrl;

    //Creates the web-client, able to be autowired into other classes w/ this and above
    @Bean
    public WebClient webClient() {
        return WebClient.create(accountUrl);
    }

    @Autowired
    private Validator validator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }

    // Standardizes how application determines host name for responses (Uses Url from gateway)
    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter()   {
        return new ForwardedHeaderFilter();
    }

}
