package com.amkor.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${data.path}")
    private String dataPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**", "/ws-data/images/**", "/favicon.ico", "/sitemap*.xml")
                .addResourceLocations("classpath:/static/assets/", ("file:" + dataPath))
//                .setCacheControl(CacheControl.maxAge(30L, TimeUnit.DAYS).cachePublic())
//                .resourceChain(true)
//                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
//                .addTransformer(new AppCacheManifestTransformer())
        ;
    }
}
