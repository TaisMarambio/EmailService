package com.challenge.emailservice.repository;

import com.challenge.emailservice.model.EmailUsage;
import com.challenge.emailservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailUsageRepository extends JpaRepository<EmailUsage, Integer> {
    Optional<EmailUsage> findByUser(User user);
Optional<EmailUsage> findByUserAndDate(User user, LocalDate date);
    List<EmailUsage> findByDateAndEmailCountGreaterThan(LocalDate date, int emailCount);
}