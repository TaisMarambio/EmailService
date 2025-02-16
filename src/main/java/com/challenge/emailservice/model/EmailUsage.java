package com.challenge.emailservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "email_usage")
public class EmailUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email_count", nullable = false)
    private int emailCount = 0;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public void incrementEmailCount() {
        this.emailCount++;
    }
}
