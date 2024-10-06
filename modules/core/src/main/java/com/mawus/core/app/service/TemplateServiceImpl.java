package com.mawus.core.app.service;

import com.mawus.core.app.exception.TemplateProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service("core_TemplateServiceImpl")
public class TemplateServiceImpl implements TemplateService {
    private final Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final Configuration freemarkerConfiguration;

    public TemplateServiceImpl(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public String processTemplate(String templateName, Map<String, Object> model) {
        StringWriter writer = new StringWriter();
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName);
            template.process(model, writer);
        } catch (IOException e) {
            log.error("Error while loading template: {}", templateName, e);
            throw new TemplateProcessingException("Template not found: " + templateName, e);
        } catch (TemplateException e) {
            log.error("Error while processing template: {}", templateName, e);
            throw new TemplateProcessingException("Error processing template: " + templateName, e);
        }
        return writer.toString();
    }
}
