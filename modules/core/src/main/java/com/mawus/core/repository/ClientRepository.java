package com.mawus.core.repository;

import com.mawus.core.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByChatId(Long chatId);

    @Transactional
    @Modifying
    @Query("update bot$Client b set b.active = ?1 where b.id = ?2")
    void updateActiveById(boolean active, UUID id);

    @Transactional
    @Modifying
    @Query("update bot$Client b set b.name = ?1 where b.id = ?2")
    void updateNameById(String name, UUID id);

    @Transactional
    @Modifying
    @Query("update bot$Client b set b.phoneNumber = ?1 where b.id = ?2")
    int updatePhoneNumberById(String phoneNumber, UUID id);
}
