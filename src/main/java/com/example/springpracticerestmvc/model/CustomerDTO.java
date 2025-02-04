package com.example.springpracticerestmvc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerDTO {
    private UUID id;
    private String name;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof CustomerDTO customerDTO)) return false;

        return Objects.equals(getId(), customerDTO.getId()) && Objects.equals(getName(), customerDTO.getName()) && Objects.equals(getVersion(), customerDTO.getVersion()) && Objects.equals(getCreatedDate(), customerDTO.getCreatedDate()) && Objects.equals(getUpdateDate(), customerDTO.getUpdateDate());
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

}
