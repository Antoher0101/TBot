package com.mawus.core.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity(name = "bot$Client")
@Table(name = "bot_client")
public class Client extends BaseUuidEntity {

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @OneToOne(mappedBy = "client")
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Trip> trips;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
