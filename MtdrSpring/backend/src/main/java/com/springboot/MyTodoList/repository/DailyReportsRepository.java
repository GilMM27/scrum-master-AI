package com.springboot.MyTodoList.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.DailyReports;
import com.springboot.MyTodoList.model.Projects;

public interface DailyReportsRepository extends JpaRepository<DailyReports, UUID> {
    
    List<DailyReports> findByProject(Projects project);

    List<DailyReports> findByProjectOrderByReportDateDesc(Projects project);

    Optional<DailyReports> findByProjectAndReportDate(Projects project, LocalDate report_date);
}
