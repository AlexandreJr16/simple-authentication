package com.example.auth.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Table(name= "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;


}
