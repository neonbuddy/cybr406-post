package com.cybr406.post.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

//Uses account project to authenticate user
@Component
public class AccountAuthenticationProvider implements AuthenticationProvider {

    private static final ParameterizedTypeReference<List<String>> typeRef =
            new ParameterizedTypeReference<List<String>>() {};  //New parameterizedType Reference (in line class creation)

    @Autowired
    private WebClient webClient;



//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        //Configure authentication to use on the database
//        auth
//                .jdbcAuthentication()
//                .dataSource(dataSource);
//    }


    //Uses account project to authenticate
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {      //Get username and pass provided
        // Update this method to verify user details using the Account microservice.
        // You will need to use the WebClient class to make calls to /check-user in Account
        try {
            List<SimpleGrantedAuthority> roles = webClient.get()                                            //Use webclient to check user
                    .uri("/check-user")
                    .headers(headers -> headers.setBasicAuth("post","post"))            //Identify ourselves as using post application
                    .header("x-username", authentication.getName())                            //Provide username and pass to check
                    .header("x-password", authentication.getCredentials().toString())
                    .retrieve()                                                                             //Retrieve response
                    .bodyToMono(typeRef)                    //Convert to a list of strings;    Generic type parameters will survive compile
                    .blockOptional(Duration.ofSeconds(5))                                //Block thread until request completes
                    .orElseThrow(() -> new Exception("No response from Account"))
                    .stream()
                    .map(SimpleGrantedAuthority::new)   //Channel all strings from simple granted auth
                    .collect(Collectors.toList());

            //Return completed Username and Password auth token, including roles
            return new UsernamePasswordAuthenticationToken(
                    authentication.getName(),
                    authentication.getCredentials(),
                    roles);
            //If anything goes wrong we will return null; this denys access
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);      //Make sure authentication object is a username/pass obj
    }

}
