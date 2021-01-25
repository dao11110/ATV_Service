package com.foxconn.fii.data.primary.repository;

import com.foxconn.fii.data.primary.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Integer> {
}
