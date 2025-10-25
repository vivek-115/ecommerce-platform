package com.example.ecomDemo.Security;

import com.example.ecomDemo.Security.jwt.AuthEntryPointJwt;
import com.example.ecomDemo.Security.jwt.AuthTokenFilter;
import com.example.ecomDemo.Security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
       DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService);
       authProvider.setPasswordEncoder(passwordEncoder());
       return authProvider;
   }

    // i needed to change the authentiation Manger and mention the AuthManger to use DaoAuthProvider in order for my Sign-in feature to work
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

       http.csrf(csrf->csrf.disable())
               .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
               .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authorizeHttpRequests(auth->
                       auth.requestMatchers("/api/auth/**").permitAll()
                               .requestMatchers("/v3/api-docs/**").permitAll()
//                              .requestMatchers("/api/admin/**").permitAll()
                               .requestMatchers("/api/public/**").permitAll()
                               .requestMatchers("/swagger-ui/**").permitAll()
                               .requestMatchers("/api/test/**").permitAll()
                               .requestMatchers("/images/**").permitAll()
                               .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // added to solve the front-end as the cookie
                               .anyRequest().authenticated()
               );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(),  UsernamePasswordAuthenticationFilter.class);
       http.headers(headers -> headers.frameOptions(
               frameOptions -> frameOptions.sameOrigin()));
        return http.build();
   }

   //Enables us to avoid/bypass security with these pattern Matchers at the Global level
   @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
       return (web -> web.ignoring().requestMatchers("/v2/api-docs",
               "/configuration/ui",
               "/swagger-resources/**",
               "/configuration/security",
               "/swagger-ui.html",
               "/webjars/**"));

   }
}
