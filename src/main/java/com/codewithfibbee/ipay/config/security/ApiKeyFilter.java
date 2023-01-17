package com.codewithfibbee.ipay.config.security;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter implements Filter {
    private final Gson gson;
    @Value("${ipay-auth-key}") String authKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authHeader = request.getHeader("Authorization");


        if (StringUtils.isBlank(authHeader) ||
            !authHeader.startsWith("Bearer ") ||
            !authHeader.substring(7).equals(authKey)) {

            servletResponse.setContentType("application/json");
            PrintWriter printWriter = servletResponse.getWriter();

            printWriter.println(
                    gson.toJson(Collections.singletonMap("error", "Unauthorized"))
            );

            printWriter.close();
        }
        log.info("convert the object to this URL is ::" + request.getRequestURI());
        log.info("Done with request sending response URL is ::" + request.getRequestURI());

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
