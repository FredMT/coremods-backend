package com.tofutracker.Coremods.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableJdbcHttpSession
@Slf4j
public class SessionConfig {

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
        resolver.setCookieSerializer(new CustomCookieSerializer());
        return resolver;
    }

    public static class CustomCookieSerializer extends DefaultCookieSerializer {
        @Override
        public void writeCookieValue(CookieValue cookieValue) {
            HttpServletRequest request = cookieValue.getRequest();

            Object rememberMeAttr = request.getAttribute("rememberMe");
            boolean rememberMe = rememberMeAttr instanceof Boolean && (Boolean) rememberMeAttr;

            if (rememberMe) {
                this.setCookieMaxAge(60 * 60 * 24 * 30); // 30 days
            }

            super.writeCookieValue(cookieValue);
        }
    }
}
