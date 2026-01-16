package com.example.aicosbackendspringboot.JWT.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/google",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/getDashboardData",  // Dashboard 資料
            "/api/getChillersData",       // 冰機資料 (注意這個 Controller 沒有 /api 前綴)
            "/api/getHistoryData",    // 歷史資料
            "/api/getChillerParam",   // 冰機參數
            "/listModels",            // 模型列表
            "/api/grafana-embed",
            "/api/getChillersData"

    };
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        // 1. 人員權限頁面/添加人員：僅限 admin
                        .requestMatchers("/api/v1/add/field").hasAuthority("admin")
                        // 2. 智慧控制頁面 API：僅限 admin 與 user (排除 other)
                        .requestMatchers("/api/chiller/**").hasAnyAuthority("admin", "user")
                        .requestMatchers("/api/getChillerParam").hasAnyAuthority("admin", "user")
                        .requestMatchers("/listModels").hasAnyAuthority("admin", "user")
                        // 3. 其他所有請求需通過身份驗證
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允許 localhost 和 nip.io
        configuration.setAllowedOrigins(Arrays.asList
                ("http://localhost:4202",
                        "http://10.25.2.130.nip.io:4202")
        );
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}