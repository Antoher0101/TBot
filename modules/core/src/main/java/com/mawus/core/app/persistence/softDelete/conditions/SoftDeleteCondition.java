package com.mawus.core.app.persistence.softDelete.conditions;

import org.hibernate.mapping.Column;

public interface SoftDeleteCondition {

    String sqlWhere(Column column, boolean nullable);
    String sqlDeleteSetter(Column column, boolean nullable);
}
