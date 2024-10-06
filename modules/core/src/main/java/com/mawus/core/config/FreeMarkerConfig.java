package com.mawus.core.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {

    @Value("${core.templates.path}")
    protected String templatePath;

    @Value("${core.templates.encoding}")
    protected String templateEncoding;

    @Bean("core_FreeMarkerConfig")
    public Configuration freemarkerConfiguration() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassForTemplateLoading(this.getClass(), templatePath);

        cfg.setDefaultEncoding(templateEncoding);

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        return cfg;
    }
}
