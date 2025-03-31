package com.example.springpracticerestmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Version
    private Long version;

    @Column(length = 50)
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "beer_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> beers = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Category category)) return false;

        return Objects.equals(getId(), category.getId()) && Objects.equals(getVersion(), category.getVersion()) && Objects.equals(getDescription(), category.getDescription()) && Objects.equals(getCreatedDate(), category.getCreatedDate()) && Objects.equals(getLastModifiedDate(), category.getLastModifiedDate()) && Objects.equals(getBeers(), category.getBeers());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getLastModifiedDate());
        result = 31 * result + Objects.hashCode(getBeers());
        return result;
    }
}