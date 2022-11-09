package com.example.api.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(
        name = "roles"
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Builder
    public Role(String name) {
        this.name = name;
    }
}
