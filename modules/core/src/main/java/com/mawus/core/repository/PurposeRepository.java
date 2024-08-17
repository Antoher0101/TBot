package com.mawus.core.repository;

import com.mawus.core.entity.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurposeRepository extends JpaRepository<Purpose, UUID> {
}
