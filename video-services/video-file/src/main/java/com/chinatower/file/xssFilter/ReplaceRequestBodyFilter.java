/*
package com.chinatower.file.xssFilter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class ReplaceRequestBodyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String contentType = request.getContentType();
        if (null != contentType && contentType.contains("multipart/")) {
            XSSCommonsMultipartResolver commonsMultipartResolver = new XSSCommonsMultipartResolver();
            XSSMultipartHttpServletRequest resolveMultipart = (XSSMultipartHttpServletRequest) commonsMultipartResolver.resolveMultipart(request);
            filterChain.doFilter(resolveMultipart, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

*/
