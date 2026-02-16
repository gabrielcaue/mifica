package com.mifica.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CorsFilter implements Filter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost",
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:8080",
        "https://gabrielcaue.github.io"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
        }

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
