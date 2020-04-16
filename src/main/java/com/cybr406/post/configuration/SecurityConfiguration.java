package com.cybr406.post.configuration;

import com.cybr406.post.security.user.AccountAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    H2SecurityConfigurer h2SecurityConfigurer;

    //Now when the application receives username and pass through http basic, they go to account application instead of database to verify
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                AccountAuthenticationProvider prov) throws Exception{
        auth.authenticationProvider(prov);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        h2SecurityConfigurer.configure(http);
        http
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET,"/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic();
    }

}
