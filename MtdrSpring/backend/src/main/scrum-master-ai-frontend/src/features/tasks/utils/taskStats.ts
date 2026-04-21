import type { TaskItem, TaskStats } from "../types/tasks.types";

export const getTaskStats = (tasks: TaskItem[]): TaskStats => {
    return {
        totalProjectTasks: tasks.length,
        totalCurrentSprintTasks: tasks.filter(task => task.status !== null).length,
        totalCompletedTasks: tasks.filter(task => task.status === "DONE").length,
        totalInReviewTasks: tasks.filter(task => task.inReview).length,
        totalBlockedTasks: tasks.filter(task => task.blocked).length,
    }
}