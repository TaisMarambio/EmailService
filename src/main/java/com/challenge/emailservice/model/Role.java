package com.challenge.emailservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users") // 🔹 Evita problemas de recursión infinita
@EqualsAndHashCode(exclude = "users") // 🔹 Evita errores en comparación de objetos
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY) // 🔹 Usa Lazy Loading para optimizar consultas
    private Set<User> users;
}
