package com.picksy.decisionservice.repository;

import com.picksy.decisionservice.model.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
