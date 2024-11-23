package com.mawus.core.jmx.logging;

public interface LoggingMBean {

    String setLogLevel(String loggerName, String level);

    String getLogLevel(String loggerName);

    String getAllLoggers();
}
