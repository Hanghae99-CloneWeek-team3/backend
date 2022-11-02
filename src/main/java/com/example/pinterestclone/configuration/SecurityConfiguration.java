package com.example.pinterestclone.configuration;


import com.example.pinterestclone.jwt.AccessDeniedHandlerException;
import com.example.pinterestclone.jwt.AuthenticationEntryPointException;
import com.example.pinterestclone.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final CorsConfig corsConfig;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
//                .antMatchers("/api/members/signin").permitAll()
//                .antMatchers("/api/members/signup").permitAll()
//                .antMatchers("/api/members/reissue").permitAll()
//                .antMatchers("/api/post").permitAll()
//                .antMatchers(HttpMethod.GET,"/api/post/{postId}").permitAll()
//                .antMatchers("/api/post/c").permitAll()
//                .antMatchers("/api/post/p").permitAll()
//                .antMatchers(HttpMethod.GET,"/api/comments/{postId}").permitAll()
//                .antMatchers("/api/comment/*").permitAll()
                .antMatchers("/api/users/signup").permitAll()
                .antMatchers("/api/users/login").permitAll()
                .antMatchers("/api/users/reissue").permitAll()

                .antMatchers("/api/posts/image").permitAll()
                .antMatchers(HttpMethod.GET,"/api/posts").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/{postId}").permitAll()
                .antMatchers(HttpMethod.GET,"/api/comments/{rootId}").permitAll()
                .antMatchers("/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .addFilter(corsConfig.corsFilter())
                .apply(new JwtSecurityConfiguration(tokenProvider));


        return http.build();
    }
}
