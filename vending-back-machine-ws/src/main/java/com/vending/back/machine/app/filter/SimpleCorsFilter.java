package com.vending.back.machine.app.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * This filter updates response with headers to allow REST calls from all domains, as intended client is running in customer browser.
 * <p>
 * vyemialyanchyk on 7.2.17.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) res;
        final HttpServletRequest request = (HttpServletRequest) req;

        final String[] allowDomain = { "http://localhost:3000", "http://localhost", };
        final Set<String> allowedOrigins = new TreeSet<String>(Arrays.asList(allowDomain));
        boolean flagAllow = false;
        String originHeader = request.getHeader("Origin");
        if (originHeader == null) {
            originHeader = request.getHeader("referer");
            if (originHeader != null) {
                for (String origin : allowedOrigins) {
                    if (originHeader.startsWith(origin)) {
                        originHeader = origin;
                        flagAllow = true;
                        break;
                    }
                }
            } else {
                final String host = request.getHeader("host");
                if (!StringUtils.isEmpty(host)) {
                    for (String origin : allowedOrigins) {
                        if (origin.contains(host)) {
                            originHeader = origin;
                            flagAllow = true;
                            break;
                        }
                    }
                }
            }
        } else if (allowedOrigins.contains(originHeader)) {
            flagAllow = true;
        }

        if (flagAllow) {
            response.setHeader("Access-Control-Allow-Origin", originHeader);
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
            response.setHeader("Access-Control-Max-Age", "3600");
            final String allowHeaders = StringUtils.join(AllowHeaders.getRealNames(), ", ");
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);

            response.setHeader(AllowHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS.getRealName(), "true");
            //response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // make Chrome prefly access control happy for authorization call
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
