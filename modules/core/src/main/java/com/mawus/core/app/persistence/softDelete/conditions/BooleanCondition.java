package com.mawus.core.app.persistence.softDelete.conditions;

import org.hibernate.mapping.Column;

public class BooleanCondition implements SoftDeleteCondition {

    @Override
    public String sqlWhere(Column column, boolean nullable) {
        if (nullable) return String.format("%s is null", column.getName());
        else return String.format("%s = false", column.getName());
    }

    @Override
    public String sqlDeleteSetter(Column column, boolean nullable) {
        return String.format("%s = true", column.getName());
    }
}
