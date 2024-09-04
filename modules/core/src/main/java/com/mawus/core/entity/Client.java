package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "bot$Client")
@Table(name = "bot_client")
public class Client extends BaseUuidEntity {

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column
    private String name;

    @Column
    private String phoneNumber;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
