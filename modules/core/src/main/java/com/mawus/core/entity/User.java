package com.mawus.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bot_user")
public class User extends BaseUuidEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
