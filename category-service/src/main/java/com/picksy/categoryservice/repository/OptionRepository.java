package com.picksy.categoryservice.repository;

import com.picksy.categoryservice.model.Category;
import com.picksy.categoryservice.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByCategory(Category category);

}
