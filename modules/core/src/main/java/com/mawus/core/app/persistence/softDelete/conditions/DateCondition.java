package com.mawus.core.app.persistence.softDelete.conditions;

import org.hibernate.mapping.Column;

public class DateCondition implements SoftDeleteCondition {

    @Override
    public String sqlWhere(Column column, boolean nullable) {
        if (nullable) {
            return String.format("%s is null", column.getName());
        } else {
            throw new IllegalStateException(String.format(
                    "Non null date columns %s unsupported for soft deletion",
                    column.getName())
            );
        }
    }

    @Override
    public String sqlDeleteSetter(Column column, boolean nullable) {
        return String.format("%s = CURRENT_DATE", column.getName());
    }
}
