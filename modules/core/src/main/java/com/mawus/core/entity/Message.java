package com.mawus.core.entity;

import com.mawus.core.domain.MessagePlaceholder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "bot$Message")
@Table(name = "bot_message")
public class Message extends BaseUuidEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(length = 4096, nullable = false)
    private String text;

    public void applyPlaceholder(MessagePlaceholder placeholder) {
        text = text.replace(placeholder.getPlaceholder(), placeholder.getReplacement().toString());
    }

    public void removeTextBetweenPlaceholder(String placeholderName) {
        text = text.replaceAll(placeholderName + "(?s).*" + placeholderName, "");
    }

    public String buildText() {
        removeAllPlaceholders();
        return text;
    }

    public void removeAllPlaceholders() {
        text = text.replaceAll("%.*%", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
