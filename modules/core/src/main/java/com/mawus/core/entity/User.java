package com.mawus.core.entity;

import jakarta.persistence.*;

@Entity(name = "bot$User")
@Table(name = "bot_user")
public class User extends StandardEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "has_link", nullable = false)
    private Boolean hasLink;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getHasLink() {
        return hasLink;
    }

    public void setHasLink(Boolean hasLink) {
        this.hasLink = hasLink;
    }
}
