package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.BurndownDataPoint;
import com.springboot.MyTodoList.dto.HistogramBucket;
import com.springboot.MyTodoList.dto.ProjectAnalyticsResponse;
import com.springboot.MyTodoList.dto.SprintAnalyticsResponse;
import com.springboot.MyTodoList.dto.TasksDoneBySprintRow;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.repository.TaskAssignmentsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private SprintsRepository sprintsRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TaskAssignmentsRepository taskAssignmentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public ResponseEntity<?> getProjectAnalytics(UUID projectId) {
        if (!projectsRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<Tasks> tasks = tasksRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        List<Sprints> sprints = sprintsRepository.findByProjectIdOrderByStartDateDesc(projectId);

        Double avgLeadTimeDays = computeAvgLeadTime(tasks);
        Double avgCycleTimeDays = computeAvgCycleTime(tasks);
        Double completionRate = computeCompletionRate(tasks);

        long blockedTasksCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.BLOCKED)
                .count();

        long delayedTasksCount = computeProjectDelayedTasks(tasks, sprints);

        // Histograms use only tasks from ACTIVE or CLOSED sprints
        List<Tasks> histogramTasks = filterTasksForHistogram(tasks, sprints);
        List<HistogramBucket> leadTimeHistogram = computeLeadTimeHistogram(histogramTasks);
        Double leadTimeMean = computeLeadTimeMean(histogramTasks);
        List<HistogramBucket> cycleTimeHistogram = computeCycleTimeHistogram(histogramTasks);
        Double cycleTimeMean = computeCycleTimeMean(histogramTasks);

        List<TasksDoneBySprintRow> tasksDoneBySprint = computeTasksDoneBySprintForProject(tasks, sprints);
        List<BurndownDataPoint> projectBurndown = computeProjectBurndownData(tasks, sprints);

        return ResponseEntity.ok(new ProjectAnalyticsResponse(
                avgLeadTimeDays,
                avgCycleTimeDays,
                completionRate,
                blockedTasksCount,
                delayedTasksCount,
                leadTimeHistogram,
                leadTimeMean,
                cycleTimeHistogram,
                cycleTimeMean,
                tasksDoneBySprint,
                projectBurndown
        ));
    }

    public ResponseEntity<?> getSprintAnalytics(UUID sprintId) {
        Optional<Sprints> sprintOpt = sprintsRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sprints sprint = sprintOpt.get();
        List<Tasks> tasks = tasksRepository.findBySprintIdOrderByCreatedAtDesc(sprintId);

        long totalTasks = tasks.size();
        long doneTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        Double sprintAccomplishment = totalTasks > 0 ? (double) doneTasks / totalTasks : null;

        Double avgCycleTimeDays = computeAvgCycleTime(tasks);

        long delayedTasksCount = computeSprintDelayedTasks(tasks, sprint);

        List<BurndownDataPoint> burndownData = computeBurndownData(tasks, sprint);

        List<HistogramBucket> leadTimeHistogram = computeLeadTimeHistogram(tasks);
        Double leadTimeMean = computeLeadTimeMean(tasks);
        List<HistogramBucket> cycleTimeHistogram = computeCycleTimeHistogram(tasks);
        Double cycleTimeMean = computeCycleTimeMean(tasks);

        List<TasksDoneBySprintRow> tasksDoneBySprint = computeTasksDoneBySprintForSprint(tasks, sprint);

        long blockedTasksCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.BLOCKED)
                .count();

        String startDateStr = sprint.getStartDate() != null ? sprint.getStartDate().toString() : null;
        String endDateStr = sprint.getEndDate() != null ? sprint.getEndDate().toString() : null;

        return ResponseEntity.ok(new SprintAnalyticsResponse(
                sprint.getSprintId(),
                sprint.getName(),
                startDateStr,
                endDateStr,
                sprintAccomplishment,
                avgCycleTimeDays,
                delayedTasksCount,
                burndownData,
                leadTimeHistogram,
                leadTimeMean,
                cycleTimeHistogram,
                cycleTimeMean,
                tasksDoneBySprint,
                blockedTasksCount
        ));
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Double computeAvgLeadTime(List<Tasks> tasks) {
        List<Double> leadTimes = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE
                        && t.getDeliveredAt() != null
                        && t.getCreatedAt() != null)
                .map(t -> ChronoUnit.HOURS.between(t.getCreatedAt(), t.getDeliveredAt()) / 24.0)
                .collect(Collectors.toList());

        if (leadTimes.isEmpty()) return null;
        return leadTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private Double computeAvgCycleTime(List<Tasks> tasks) {
        List<Double> cycleTimes = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE
                        && t.getStartedAt() != null
                        && t.getDeliveredAt() != null)
                .map(t -> ChronoUnit.HOURS.between(t.getStartedAt(), t.getDeliveredAt()) / 24.0)
                .collect(Collectors.toList());

        if (cycleTimes.isEmpty()) return null;
        return cycleTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private Double computeCompletionRate(List<Tasks> tasks) {
        if (tasks.isEmpty()) return null;
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        return (double) done / tasks.size();
    }

    private long computeProjectDelayedTasks(List<Tasks> tasks, List<Sprints> sprints) {
        LocalDate today = LocalDate.now();
        Set<UUID> pastSprintIds = sprints.stream()
                .filter(s -> s.getEndDate() != null && s.getEndDate().isBefore(today))
                .map(Sprints::getSprintId)
                .collect(Collectors.toSet());

        return tasks.stream()
                .filter(t -> t.getSprintId() != null
                        && pastSprintIds.contains(t.getSprintId())
                        && t.getStatus() != TaskStatus.DONE)
                .count();
    }

    private long computeSprintDelayedTasks(List<Tasks> tasks, Sprints sprint) {
        LocalDate today = LocalDate.now();
        if (sprint.getEndDate() == null || !sprint.getEndDate().isBefore(today)) {
            return 0L;
        }
        return tasks.stream().filter(t -> t.getStatus() != TaskStatus.DONE).count();
    }

    // ── histogram helpers ─────────────────────────────────────────────────────

    /** Returns tasks that belong to ACTIVE or CLOSED sprints (excludes PLANNED). */
    private List<Tasks> filterTasksForHistogram(List<Tasks> allTasks, List<Sprints> sprints) {
        Set<UUID> relevantIds = sprints.stream()
                .filter(s -> s.getStatus() == SprintStatus.ACTIVE || s.getStatus() == SprintStatus.CLOSED)
                .map(Sprints::getSprintId)
                .collect(Collectors.toSet());
        return allTasks.stream()
                .filter(t -> t.getSprintId() != null && relevantIds.contains(t.getSprintId()))
                .collect(Collectors.toList());
    }

    private List<Integer> extractLeadTimeDays(List<Tasks> tasks) {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE
                        && t.getDeliveredAt() != null
                        && t.getCreatedAt() != null)
                .map(t -> {
                    double hours = ChronoUnit.HOURS.between(t.getCreatedAt(), t.getDeliveredAt());
                    return (int) Math.ceil(hours / 24.0);
                })
                .filter(d -> d > 0)
                .collect(Collectors.toList());
    }

    private List<Integer> extractCycleTimeDays(List<Tasks> tasks) {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE
                        && t.getStartedAt() != null
                        && t.getDeliveredAt() != null)
                .map(t -> {
                    double hours = ChronoUnit.HOURS.between(t.getStartedAt(), t.getDeliveredAt());
                    return (int) Math.ceil(hours / 24.0);
                })
                .filter(d -> d > 0)
                .collect(Collectors.toList());
    }

    private List<HistogramBucket> buildHistogram(List<Integer> days) {
        if (days.isEmpty()) return Collections.emptyList();
        int min = days.stream().mapToInt(d -> d).min().orElse(1);
        int max = days.stream().mapToInt(d -> d).max().orElse(1);
        Map<Integer, Long> grouped = days.stream()
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        List<HistogramBucket> buckets = new ArrayList<>();
        for (int d = min; d <= max; d++) {
            buckets.add(new HistogramBucket(d, grouped.getOrDefault(d, 0L)));
        }
        return buckets;
    }

    private List<HistogramBucket> computeLeadTimeHistogram(List<Tasks> tasks) {
        return buildHistogram(extractLeadTimeDays(tasks));
    }

    private Double computeLeadTimeMean(List<Tasks> tasks) {
        List<Integer> days = extractLeadTimeDays(tasks);
        if (days.isEmpty()) return null;
        return days.stream().mapToInt(d -> d).average().orElse(0.0);
    }

    private List<HistogramBucket> computeCycleTimeHistogram(List<Tasks> tasks) {
        return buildHistogram(extractCycleTimeDays(tasks));
    }

    private Double computeCycleTimeMean(List<Tasks> tasks) {
        List<Integer> days = extractCycleTimeDays(tasks);
        if (days.isEmpty()) return null;
        return days.stream().mapToInt(d -> d).average().orElse(0.0);
    }

    private List<BurndownDataPoint> computeBurndownData(List<Tasks> tasks, Sprints sprint) {
        if (sprint.getStartDate() == null) return Collections.emptyList();

        LocalDate startDate = sprint.getStartDate();
        LocalDate endDate = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDate.now();
        LocalDate today = LocalDate.now();
        LocalDate chartEnd = endDate.isBefore(today) ? endDate : today;

        int totalPoints = tasks.stream()
                .mapToInt(t -> t.getStoryPoints() != null ? t.getStoryPoints() : 0)
                .sum();

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) totalDays = 1;

        List<BurndownDataPoint> burndown = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(chartEnd)) {
            final LocalDate day = current;

            int completedPoints = tasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.DONE)
                    .filter(t -> {
                        if (t.getDeliveredAt() != null) {
                            return !t.getDeliveredAt().toLocalDate().isAfter(day);
                        }
                        return !day.isBefore(today);
                    })
                    .mapToInt(t -> t.getStoryPoints() != null ? t.getStoryPoints() : 0)
                    .sum();

            long dayIndex = ChronoUnit.DAYS.between(startDate, day);
            double remaining = Math.max(0, totalPoints - completedPoints);
            double ideal = totalPoints * (1.0 - (double) dayIndex / totalDays);

            burndown.add(new BurndownDataPoint(day.toString(), remaining, Math.max(0, ideal)));
            current = current.plusDays(1);
        }

        return burndown;
    }

    // ── tasks-done-by-sprint helpers ─────────────────────────────────────────

    /**
     * For project scope: CLOSED + ACTIVE sprints, ordered by startDate.
     * For each sprint, counts DONE tasks per assigned user.
     */
    private List<TasksDoneBySprintRow> computeTasksDoneBySprintForProject(
            List<Tasks> tasks, List<Sprints> sprints) {

        List<Sprints> relevant = sprints.stream()
                .filter(s -> s.getStatus() == SprintStatus.CLOSED || s.getStatus() == SprintStatus.ACTIVE)
                .sorted(Comparator.comparing(s -> s.getStartDate() != null ? s.getStartDate() : LocalDate.MIN))
                .collect(Collectors.toList());

        Map<UUID, List<Tasks>> tasksBySprint = tasks.stream()
                .filter(t -> t.getSprintId() != null)
                .collect(Collectors.groupingBy(Tasks::getSprintId));

        return relevant.stream()
                .map(sprint -> buildTasksDoneRow(sprint,
                        tasksBySprint.getOrDefault(sprint.getSprintId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    /** For sprint scope: single sprint row. */
    private List<TasksDoneBySprintRow> computeTasksDoneBySprintForSprint(
            List<Tasks> tasks, Sprints sprint) {
        return Collections.singletonList(buildTasksDoneRow(sprint, tasks));
    }

    private TasksDoneBySprintRow buildTasksDoneRow(Sprints sprint, List<Tasks> sprintTasks) {
        List<Tasks> doneTasks = sprintTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .collect(Collectors.toList());

        Map<UUID, Long> countsByUserId = new LinkedHashMap<>();
        for (Tasks task : doneTasks) {
            List<TaskAssignments> assignments = taskAssignmentsRepository.findByTaskId(task.getTaskId());
            for (TaskAssignments a : assignments) {
                countsByUserId.merge(a.getUserId(), 1L, Long::sum);
            }
        }

        Map<UUID, String> usernames = new HashMap<>();
        if (!countsByUserId.isEmpty()) {
            usersRepository.findAllById(new ArrayList<>(countsByUserId.keySet()))
                    .forEach(u -> usernames.put(u.getUserId(), u.getUsername()));
        }

        Map<String, Long> tasksDoneByUser = new LinkedHashMap<>();
        countsByUserId.forEach((uid, count) ->
                tasksDoneByUser.put(usernames.getOrDefault(uid, uid.toString()), count));

        return new TasksDoneBySprintRow(sprint.getSprintId(), sprint.getName(), tasksDoneByUser);
    }

    /**
     * Project-scope burndown: uses ALL sprints (CLOSED, ACTIVE, PLANNED).
     * X-axis: min(startDate) → max(endDate), capped at today.
     */
    private List<BurndownDataPoint> computeProjectBurndownData(
            List<Tasks> tasks, List<Sprints> sprints) {

        LocalDate minStart = sprints.stream()
                .map(Sprints::getStartDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        LocalDate maxEnd = sprints.stream()
                .map(Sprints::getEndDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (minStart == null) return Collections.emptyList();

        LocalDate today = LocalDate.now();
        LocalDate endDate = maxEnd != null ? maxEnd : today;
        LocalDate chartEnd = endDate.isBefore(today) ? endDate : today;

        int totalPoints = tasks.stream()
                .mapToInt(t -> t.getStoryPoints() != null ? t.getStoryPoints() : 0)
                .sum();

        long totalDays = ChronoUnit.DAYS.between(minStart, endDate);
        if (totalDays <= 0) totalDays = 1;

        List<BurndownDataPoint> burndown = new ArrayList<>();
        LocalDate current = minStart;
        while (!current.isAfter(chartEnd)) {
            final LocalDate day = current;
            int completedPoints = tasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.DONE)
                    .filter(t -> t.getDeliveredAt() != null
                            && !t.getDeliveredAt().toLocalDate().isAfter(day))
                    .mapToInt(t -> t.getStoryPoints() != null ? t.getStoryPoints() : 0)
                    .sum();

            long dayIndex = ChronoUnit.DAYS.between(minStart, day);
            double remaining = Math.max(0, totalPoints - completedPoints);
            double ideal = totalPoints * (1.0 - (double) dayIndex / totalDays);

            burndown.add(new BurndownDataPoint(day.toString(), remaining, Math.max(0, ideal)));
            current = current.plusDays(1);
        }
        return burndown;
    }
}
