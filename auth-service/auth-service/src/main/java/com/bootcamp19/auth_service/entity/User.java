package com.bootcamp19.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mst_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;
}
