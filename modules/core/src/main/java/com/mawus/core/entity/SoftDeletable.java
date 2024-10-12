package com.mawus.core.entity;

import java.time.LocalDateTime;

public interface SoftDeletable {

    Boolean isDeleted();

    LocalDateTime getDeleteTs();

    void setDeleteTs(LocalDateTime deleteTs);

    String getDeletedBy();

    void setDeletedBy(String deletedBy);
}
