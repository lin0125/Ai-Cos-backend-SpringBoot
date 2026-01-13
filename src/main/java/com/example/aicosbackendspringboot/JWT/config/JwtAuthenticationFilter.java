package com.example.aicosbackendspringboot.JWT.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.aicosbackendspringboot.JWT.base.Role; // 確保有 import 這行
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Qualifier("userDetailsService")
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.equals("/api/v1/auth/google") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userAccount;
        final String userType;

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

        // 確保這裡轉成字串，避免 null
        userAccount = String.valueOf(jwtService.extractUserName(jwt));
        userType = String.valueOf(jwtService.extractUserType(jwt));

        if (!userAccount.isEmpty() && !userType.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = null;

            // ★★★ 修改重點開始 ★★★
            // 判斷是否為 user 或 admin (忽略大小寫)
            // 這裡假設你的 UserDetailsService 可以同時處理 user 和 admin 的帳號查詢
            if ("user".equalsIgnoreCase(userType) ||
                    "admin".equalsIgnoreCase(userType) ||
                    Role.user.name().equalsIgnoreCase(userType)) {

                userDetails = userDetailsService.loadUserByUsername(userAccount);

            } else {
                log.warn("JWT token contains unknown userType: {}", userType);
            }
            // ★★★ 修改重點結束 ★★★

            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}