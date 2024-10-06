package com.mawus.core.app.service;

import java.util.Map;

public interface TemplateService {

    String processTemplate(String templateName, Map<String, Object> model);
}
