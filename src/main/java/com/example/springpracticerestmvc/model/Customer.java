package com.example.springpracticerestmvc.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    public Customer(UUID id,
                    String name,
                    Integer version,
                    LocalDateTime createdDate,
                    LocalDateTime updateDate) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Customer customer)) return false;

        return Objects.equals(getId(), customer.getId()) && Objects.equals(getName(), customer.getName()) && Objects.equals(getVersion(), customer.getVersion()) && Objects.equals(getCreatedDate(), customer.getCreatedDate()) && Objects.equals(getUpdateDate(), customer.getUpdateDate());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getUpdateDate());
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
