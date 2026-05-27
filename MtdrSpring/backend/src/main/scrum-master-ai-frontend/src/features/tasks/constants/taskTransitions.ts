import type { TaskStatus } from "../types/tasks.types";

/**
 * Canonical task status transition rules — shared by the form dialog and
 * the sprint board (drag-and-drop). Both UX surfaces enforce the same rules.
 */
export const VALID_TASK_STATUS_TRANSITIONS: Record<TaskStatus, TaskStatus[]> =
  {
    TO_DO: ["IN_PROGRESS", "BLOCKED"],
    IN_PROGRESS: ["REVIEW", "BLOCKED", "TO_DO"],
    REVIEW: ["IN_PROGRESS", "DONE", "BLOCKED"],
    BLOCKED: ["TO_DO", "IN_PROGRESS", "REVIEW"],
    DONE: [],
  };
