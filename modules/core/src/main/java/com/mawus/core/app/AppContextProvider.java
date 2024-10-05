package com.mawus.core.app;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("core_AppContextProvider")
public class AppContextProvider implements ApplicationContextAware {
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContextProvider.appContext = applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }
}
