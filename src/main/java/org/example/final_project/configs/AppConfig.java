package org.example.final_project.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class AppConfig {
	
	@Autowired
	private CorsConfigurationSource corsConfigurationSource;
	
	@Bean
	public SecurityFilterChain securityConfiguration(HttpSecurity http) throws Exception {
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeHttpRequests()
		.requestMatchers(HttpMethod.POST,"/signup").permitAll()
		.requestMatchers(HttpMethod.GET,"/api").permitAll()
		.requestMatchers(
			"/websocket_test_client.html",
			"/static/**",
			"/js/**",
			"/css/**",
			"/images/**",
			"/ws/**",
			"/ws",
			"/favicon.io"
		).permitAll()
		.anyRequest().authenticated()
		.and()
		.addFilterAfter(new JwtGenratorFilter(), BasicAuthenticationFilter.class)
		.addFilterBefore(new JwtValidationFilter(), BasicAuthenticationFilter.class)
		.exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        .and()
		.csrf().disable()
		.cors().configurationSource(corsConfigurationSource)
		.and()
		.formLogin()
		.and()
		.httpBasic();
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

