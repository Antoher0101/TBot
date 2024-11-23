package com.mawus.core.jmx.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ManagedResource(objectName = "com.mawus.core.logging.logging:type=LoggingMBean", description = "Information on the management interface of the MBean")
public class LoggingMBeanImpl implements LoggingMBean {

    private final LoggerContext context;

    public LoggingMBeanImpl() {
        this.context = (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    @Override
    @ManagedOperation(description = "Set the log level for a specific logger")
    public String setLogLevel(String loggerName, String level) {
        Logger logger = context.getLogger(loggerName);
        if (logger != null) {
            logger.setLevel(Level.toLevel(level));
            return String.format("Logger [%s] level changed to [%s]%n", loggerName, level);
        } else {
            return String.format("Logger [%s] not found%n", loggerName);
        }
    }

    @Override
    @ManagedOperation(description = "Get the current log level for a specific logger")
    public String getLogLevel(String loggerName) {
        Logger logger = context.getLogger(loggerName);
        return (logger != null && logger.getLevel() != null) ? logger.getLevel().toString() : "NOT_SET";
    }

    @ManagedOperation(description = "Get list of all loggers")
    public String getAllLoggers() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggers = loggerContext.getLoggerList();
        return loggers.stream().map(Logger::getName).collect(Collectors.joining(", \n"));
    }
}
