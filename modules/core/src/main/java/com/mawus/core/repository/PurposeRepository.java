package com.mawus.core.repository;

import com.mawus.core.entity.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, UUID> {
}
