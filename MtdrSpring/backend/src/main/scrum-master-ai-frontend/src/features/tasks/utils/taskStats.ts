import type { TaskItem, TaskStats } from "../types/tasks.types";

export const getTaskStats = (tasks: TaskItem[], activeSprintId?: string | null): TaskStats => {
    return {
        totalProjectTasks: tasks.length,
        totalCurrentSprintTasks: activeSprintId
            ? tasks.filter(task => task.sprintId === activeSprintId).length
            : 0,
        totalCompletedTasks: tasks.filter(task => task.status === "DONE").length,
        totalInReviewTasks: tasks.filter(task => task.inReview).length,
        totalBlockedTasks: tasks.filter(task => task.blocked).length,
    }
}