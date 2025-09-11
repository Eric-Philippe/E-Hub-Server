package org.ericp.ehub.server.config

import org.ericp.ehub.server.filter.ConditionalApiKeyAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val conditionalApiKeyAuthFilter: ConditionalApiKeyAuthFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v2/api-docs",
                        "/webjars/**",
                        "/api/health/**",
                        "/actuator/health/**",
                        "/actuator/info/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(conditionalApiKeyAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
