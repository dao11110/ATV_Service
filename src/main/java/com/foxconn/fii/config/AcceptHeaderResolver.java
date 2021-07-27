package com.foxconn.fii.config;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class AcceptHeaderResolver extends AcceptHeaderLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Cookie lang = WebUtils.getCookie(request, "lang");
        if (lang == null) {
            return super.resolveLocale(request);
        }
        return Locale.forLanguageTag(lang.getValue());
    }
}
