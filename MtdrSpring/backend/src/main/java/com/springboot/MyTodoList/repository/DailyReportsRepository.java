package com.springboot.MyTodoList.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.DailyReports;

public interface DailyReportsRepository extends JpaRepository<DailyReports, UUID> {
    
    List<DailyReports> findByProjectId(UUID projectId);

    List<DailyReports> findByProjectIdOrderByReportDateDesc(UUID projectId);

    Optional<DailyReports> findByProjectIdAndReportDate(UUID projectId, LocalDate reportDate);
}
