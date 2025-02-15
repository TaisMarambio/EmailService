package com.challenge.emailservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDate;

@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email =:email")
@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username =:username")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "email_count", nullable = false)
    private int emailCount = 0;  //contador para emails enviados en el día

    @Column(name = "last_email_sent_date")
    private LocalDate lastEmailSentDate;  //Fecha del ultimo mail enviado
}
