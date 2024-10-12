package com.mawus.core.app.persistence.softDelete;

import com.mawus.core.app.persistence.softDelete.conditions.BooleanCondition;
import com.mawus.core.app.persistence.softDelete.conditions.DateCondition;
import com.mawus.core.app.persistence.softDelete.conditions.SoftDeleteCondition;
import com.mawus.core.app.utils.AnnotationUtils;
import org.hibernate.mapping.*;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;
import java.util.stream.Stream;

public class SoftDeleteConditions {

    protected Map<Class, SoftDeleteCondition> conditionMap = new HashMap<>();

    public SoftDeleteConditions(SessionFactoryServiceRegistry serviceRegistry) {
        conditionMap.put(Boolean.class, new BooleanCondition());
        conditionMap.put(LocalDateTime.class, new DateCondition());
    }

    private String softDeletionPropertyOperation(PersistentClass entityBinding, SoftDeletionOperation operation) {
        Map.Entry<Column, Class> deleteProperty = findSoftDeletedProperty(entityBinding);
        if (deleteProperty == null) {
            return null;
        }
        Class propertyClass = deleteProperty.getValue();
        Column column = deleteProperty.getKey();

        Class key = conditionMap.containsKey(propertyClass)
                ? propertyClass
                : conditionMap.keySet().stream().filter(clazz -> clazz.isAssignableFrom(propertyClass)).findFirst().orElse(null);
        if (key == null) {
            throw new IllegalStateException(String.format(
                    "Unsupported soft deletion property '%s' class %s (class: %s)",
                    deleteProperty.getKey(),
                    propertyClass,
                    entityBinding.getClassName())
            );
        }

        SoftDeleteCondition condition = conditionMap.get(key);

        if (column!=null){
            return operation.apply(condition, column, column.isNullable());
        } else {
            throw new IllegalStateException(String.format(
                    "Soft deletion property should be mapped to column (class: %s)",
                    entityBinding.getClassName())
            );
        }
    }

    private Map.Entry<Column, Class> findSoftDeletedProperty(PersistentClass persistentClass) {
        SoftDelete softDelete = AnnotationUtils.findTypeAnnotation(persistentClass.getMappedClass(), SoftDelete.class);
        if (!softDelete.property().isEmpty()) {
            return new AbstractMap.SimpleEntry<>(findPropertyColumn(persistentClass, softDelete.property()), softDelete.type());
        }

        return Stream.of(persistentClass.getMappedClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(SoftDeleteColumn.class) != null)
                .findFirst()
                .map(f -> new AbstractMap.SimpleEntry<Column, Class>(findPropertyColumn(persistentClass, f.getName()), f.getType()))
                .orElse(null);
    }

    private Column findPropertyColumn(PersistentClass persistentClass, String propertyName) {
        Property property = persistentClass.getProperty(propertyName);
        Value value = property.getValue();
        if (value instanceof SimpleValue simpleValue) {
            if (simpleValue.getColumns().iterator().hasNext()) {
                return simpleValue.getColumns().iterator().next();
            }
        } else {
            throw new IllegalStateException(String.format(
                    "Soft deletion property '%s' should have primitive type (class: %s)",
                    property.getName(),
                    persistentClass.getClassName())
            );
        }
        return null;
    }

    public String getSoftDeleteWhere(PersistentClass entityBinding) {
        return softDeletionPropertyOperation(entityBinding, SoftDeleteCondition::sqlWhere);
    }

    public String getSQLDeleteSetter(PersistentClass entityBinding) {
        return softDeletionPropertyOperation(entityBinding, SoftDeleteCondition::sqlDeleteSetter);
    }

    @FunctionalInterface
    private interface SoftDeletionOperation {
        String apply(SoftDeleteCondition condition, Column column, boolean isNullable);
    }
}
