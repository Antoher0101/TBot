package com.mawus.core.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JmxService {

    private static final Logger log = LoggerFactory.getLogger(JmxService.class);
    private final MBeanServer mBeanServer;

    public JmxService() {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Возвращает список всех зарегистрированных MBeans.
     */
    public Set<ObjectName> getMBeans() {
        try {
            return mBeanServer.queryNames(null, null);
        } catch (Exception e) {
            log.error("Error while getting MBeans", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Вызывает указанный метод у MBean.
     *
     * @param objectName   Имя MBean.
     * @param operation    Название метода.
     * @param params       Параметры метода.
     * @param signature    Типы параметров метода.
     * @return Результат выполнения метода.
     */
    public Object invoke(String objectName, String operation, Object[] params, String[] signature) {
        try {
            log.trace("Invoking MBean: {}", operation);
            return mBeanServer.invoke(new ObjectName(objectName), operation, params, signature);
        } catch (Exception e) {
            log.error("Error while invoking MBean: {}", operation, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает список методов указанного MBean.
     *
     * @param objectName Имя MBean.
     * @return Список методов.
     */
    public Set<MBeanOperationInfo> getOperations(String objectName) {
        try {
            MBeanInfo info = mBeanServer.getMBeanInfo(new ObjectName(objectName));
            return Set.of(info.getOperations());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении информации о методах MBean", e);
        }
    }

    /**
     * Возвращает значение указанного атрибута MBean.
     *
     * @param objectName Имя MBean.
     * @param attribute  Название атрибута.
     * @return Значение атрибута.
     */
    public Object getAttribute(String objectName, String attribute) {
        try {
            return mBeanServer.getAttribute(new ObjectName(objectName), attribute);
        } catch (Exception e) {
            log.error("Error while getting MBean attribute: {}", attribute, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает все атрибуты MBean.
     *
     * @param objectName Имя MBean.
     * @return Список атрибутов.
     */
    public Map<String, String> getAttributes(String objectName) {
        try {
            ObjectName oN = new ObjectName(objectName);
            MBeanInfo info = mBeanServer.getMBeanInfo(oN);
            return Arrays.stream(info.getAttributes())
                    .filter(MBeanAttributeInfo::isReadable)
                    .collect(Collectors.toMap(
                            MBeanAttributeInfo::getName,
                            attr -> {
                                try {
                                    return String.valueOf(mBeanServer.getAttribute(oN, attr.getName()));
                                } catch (Exception e) {
                                    return "N/A";
                                }
                            }
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении атрибутов MBean", e);
        }
    }
}
