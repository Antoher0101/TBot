package com.mawus.core.entity;

import com.mawus.core.app.persistence.softDelete.SoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
@SoftDelete(property = "deleteTs", type = LocalDateTime.class)
public abstract class StandardEntity extends BaseUuidEntity implements SoftDeletable {

    @Column(name = "delete_ts")
    private LocalDateTime deleteTs;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Override
    public LocalDateTime getDeleteTs() {
        return deleteTs;
    }

    @Override
    public void setDeleteTs(LocalDateTime deleteTs) {
        this.deleteTs = deleteTs;
    }

    @Override
    public String getDeletedBy() {
        return deletedBy;
    }

    @Override
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public Boolean isDeleted() {
        return deleteTs != null;
    }
}
