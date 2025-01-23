package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "bot$Message")
@Table(name = "bot_message", uniqueConstraints = {
        @UniqueConstraint(name = "uc_message_key", columnNames = {"key"})
})
public class Message extends BaseUuidEntity {

    @Column(nullable = false, unique = true)
    private String key;

    @Column
    private String description;

    @Column(length = 4096, nullable = false)
    private String text;

    public Message() {
    }

    public Message(String key, String description, String text) {
        this(key, text);
        this.description = description;
    }

    public Message(String key, String text) {
        setId(UUID.randomUUID());
        setCreateTs(LocalDateTime.now());
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String name) {
        this.key = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
