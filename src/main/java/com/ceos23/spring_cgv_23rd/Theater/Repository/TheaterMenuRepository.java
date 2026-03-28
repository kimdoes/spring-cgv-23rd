package com.ceos23.spring_cgv_23rd.Theater.Repository;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterMenuRepository extends JpaRepository<TheaterMenu, Long> {
    List<TheaterMenu> findByTheater(Theater theater);
}
