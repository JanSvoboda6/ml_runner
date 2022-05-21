package com.jan.web.security.configuration;

import com.jan.web.security.authentication.AuthenticationEntryPointJwt;
import com.jan.web.security.authentication.AuthorizationTokenFilter;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final AuthenticationEntryPointJwt unauthorizedHandler;
    private final UserRepository userRepository;

    public WebSecurityConfig(JsonWebTokenUtility jsonWebTokenUtility, AuthenticationEntryPointJwt unauthorizedHandler, UserRepository userRepository)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.unauthorizedHandler = unauthorizedHandler;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthorizationTokenFilter authenticationJwtTokenFilter()
    {
        return new AuthorizationTokenFilter(jsonWebTokenUtility, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests().antMatchers("/console/**").permitAll();
        http.csrf().ignoringAntMatchers("/console/**");
        http.authorizeRequests().antMatchers("/api/runner/**").permitAll();
        http.csrf().ignoringAntMatchers("/api/runner/**");
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests().antMatchers("/api/**").permitAll();
        http.csrf().ignoringAntMatchers("/api/**");
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}