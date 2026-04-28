import { apiClient } from "../../../services/Api";
import { API_ENDPOINTS } from "../../../services/Endpoints";
import type {
  SprintItem,
  CreateSprintPayload,
  UpdateSprintPayload,
  SprintSummaryData,
} from "../types/sprint.types";
import type { TaskItem, TaskStatus } from "../../tasks/types/tasks.types";

export const getProjectSprints = async (
  projectId: string,
): Promise<SprintItem[]> => {
  const response = await apiClient.get<SprintItem[]>(
    API_ENDPOINTS.sprints.byProject(projectId),
  );
  return response.data;
};

export const createSprint = async (
  payload: CreateSprintPayload,
): Promise<SprintItem> => {
  const response = await apiClient.post<SprintItem>(
    API_ENDPOINTS.sprints.create,
    payload,
  );
  return response.data;
};

export const updateSprint = async (
  sprintId: string,
  payload: UpdateSprintPayload,
): Promise<SprintItem> => {
  const response = await apiClient.put<SprintItem>(
    API_ENDPOINTS.sprints.update(sprintId),
    payload,
  );
  return response.data;
};

export const getSprintSummary = async (
  sprintId: string,
): Promise<SprintSummaryData> => {
  const response = await apiClient.get<SprintSummaryData>(
    API_ENDPOINTS.sprints.summary(sprintId),
  );
  return response.data;
};

export const getSprintTasks = async (sprintId: string): Promise<TaskItem[]> => {
  const response = await apiClient.get<TaskItem[]>(
    API_ENDPOINTS.tasks.bySprint(sprintId),
  );
  return response.data;
};

export const updateTaskStatus = async (
  taskId: string,
  status: TaskStatus,
): Promise<TaskItem> => {
  const response = await apiClient.patch<TaskItem>(
    API_ENDPOINTS.tasks.updateStatus(taskId),
    { status },
  );
  return response.data;
};
