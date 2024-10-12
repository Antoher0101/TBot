package com.mawus.core.app.persistence.softDelete;

import jakarta.persistence.ManyToMany;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.*;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;


import static java.lang.String.format;

public class SoftDeleteHibernateMetadataIntegrator implements Integrator {

    private Logger log = LoggerFactory.getLogger(SoftDeleteHibernateMetadataIntegrator.class.getName());

    private static final String AND_IS_NOT_DELETED_CONDITION = "(%s) AND %s";
    private static final String SOFT_DELETION_SQL_CONDITION = "UPDATE %s SET %s WHERE %s = ?";

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        applySoftDeletion(metadata, new SoftDeleteConditions(serviceRegistry));
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }

    private void applySoftDeletion(Metadata metadata, SoftDeleteConditions softDeleteConditions) {
        for (PersistentClass entityBinding : metadata.getEntityBindings()) {
            if (isSoftDeletable(entityBinding)) {
                applySoftDeletion(entityBinding, softDeleteConditions);
                if (entityBinding instanceof RootClass) {
                    applyWhere((RootClass) entityBinding, softDeleteConditions);
                } else {
                    log.warn(String.format("@Where condition was not applied to inherited entity %s",entityBinding.getClassName()));
                }
            }
            applySoftDeletionFilterForLinkedEntities(metadata, entityBinding, softDeleteConditions);
        }
    }

    private void applySoftDeletionFilterForLinkedEntities(Metadata metadata, PersistentClass entityBinding, SoftDeleteConditions softDeleteConditions) {
        for (Iterator it = entityBinding.getProperties().iterator(); it.hasNext(); ) {
            Property property = (Property) it.next();
            Type propertyType = property.getType();
            if (propertyType.isCollectionType() && property.getValue() instanceof Bag) {
                Bag bag = (Bag) property.getValue();
                PersistentClass propertyClass = getPropertyClass(bag.getElement(), metadata);
                if (propertyClass != null && isSoftDeletable(propertyClass)) {
                    String where = softDeleteConditions.getSoftDeleteWhere(propertyClass);
                    if (where != null) {
                        if (isManyToMany(property)) {
                            applyIgnoreNotFound(bag);
                            addManyToManyWhereCondition(bag, where);
                        } else {
                            addWhereCondition(bag, where);
                        }
                    }
                }

            }
            if (propertyType.isAssociationType() && !propertyType.isCollectionType()) {
                PersistentClass propertyClass = getPropertyClass(property.getValue(), metadata);
                if (propertyClass != null && isSoftDeletable(propertyClass) && property.getValue() instanceof ManyToOne) {
                    ManyToOne manyToOne = (ManyToOne) property.getValue();
                    manyToOne.setIgnoreNotFound(true);
                }
            }
        }
    }

    private PersistentClass getPropertyClass(Value element, Metadata metadata) {
        if (element != null) {
            if (element instanceof OneToMany) {
                return ((OneToMany) element).getAssociatedClass();
            }
            if (element instanceof ManyToOne) {
                return metadata.getEntityBinding(((ManyToOne) element).getReferencedEntityName());
            }
        }
        return null;
    }


    private void applyIgnoreNotFound(Bag bag) {
        if (bag.getElement() instanceof ManyToOne) {
            ManyToOne manyToOne = (ManyToOne) bag.getElement();
            manyToOne.setIgnoreNotFound(true);
        }
    }

    private void addWhereCondition(Filterable bag, String where) {
        if (bag instanceof Collection) {
            Collection collection = (Collection) bag;
            if (collection.getWhere() != null) {
                collection.setWhere(format(
                        AND_IS_NOT_DELETED_CONDITION,
                        collection.getWhere(), where));
            } else {
                collection.setWhere(where);
            }
        }
    }

    private void addManyToManyWhereCondition(Filterable bag, String where) {
        if (bag instanceof Collection) {
            Collection collection = (Collection) bag;
            if (collection.getManyToManyWhere() != null) {
                collection.setManyToManyWhere(format(
                        AND_IS_NOT_DELETED_CONDITION,
                        collection.getManyToManyWhere(), where));
            } else {
                collection.setManyToManyWhere(where);
            }
        }
    }

    private boolean isManyToMany(Property field) {
        try {
            return field.getPersistentClass().getMappedClass().getDeclaredField(field.getName()).getAnnotation(ManyToMany.class) != null;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private void applySoftDeletion(PersistentClass entityBinding, SoftDeleteConditions softDeleteConditions) {
        applySQLDelete(entityBinding, softDeleteConditions);
    }

    @SuppressWarnings("removal")
    private void applySQLDelete(PersistentClass entityBinding, SoftDeleteConditions softDeleteConditions) {
        if (entityBinding.getCustomSQLDelete() == null) {
            String tableName = entityBinding.getTable().getName();
            String softDeleteSetter = softDeleteConditions.getSQLDeleteSetter(entityBinding);
            String idColumn = findIdColumn(entityBinding);
            String customSQLDelete = String.format(SOFT_DELETION_SQL_CONDITION, tableName, softDeleteSetter, idColumn);
            entityBinding.setCustomSQLDelete(customSQLDelete, false, ExecuteUpdateResultCheckStyle.NONE);
        } else {
            log.info(String.format("Soft deletable entity %s already has @SQLDelete annotation", entityBinding.getEntityName()));
        }
    }

    private String findIdColumn(PersistentClass entityBinding) {
        Column column = entityBinding.getIdentifierProperty().getColumns().iterator().next();
        return column.getName();
    }

    private void applyWhere(RootClass entityBinding, SoftDeleteConditions softDeleteConditions) {
        String where = softDeleteConditions.getSoftDeleteWhere(entityBinding);
        if (entityBinding.getWhere() != null) {
            entityBinding.setWhere(format(
                    AND_IS_NOT_DELETED_CONDITION,
                    entityBinding.getWhere(), where)
            );
        } else {
            entityBinding.setWhere(where);
        }
    }

    private boolean isSoftDeletable(PersistentClass entityClass) {
        Class<?> mappedClass = entityClass.getMappedClass();
        return hasSoftDeleteAnnotation(mappedClass);
    }

    private boolean hasSoftDeleteAnnotation(Class<?> clazz) {
        if (clazz.getAnnotation(SoftDelete.class) != null) {
            return true;
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            return hasSoftDeleteAnnotation(superclass);
        }

        return false;
    }
}
