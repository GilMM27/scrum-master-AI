import { apiClient } from '../../../services/Api';
import { API_ENDPOINTS } from '../../../services/Endpoints';
import type { CreateTaskPayload, SprintOption, TaskAssignee, TaskDetailItem, TaskItem, UpdateTaskPayload } from '../types/tasks.types';

export const getProjectTasks = async (projectId: string): Promise<TaskItem[]> => {
    const response = await apiClient.get<TaskItem[]>(API_ENDPOINTS.tasks.byProject(projectId));
    return response.data;
};

export const getTaskDetails = async (taskId: string): Promise<TaskDetailItem> => {
    const response = await apiClient.get<TaskDetailItem>(API_ENDPOINTS.tasks.byId(taskId));
    return response.data;
};

export const getProjectDevelopers = async (projectId: string): Promise<TaskAssignee[]> => {
    const response = await apiClient.get<TaskAssignee[]>(API_ENDPOINTS.projects.developers(projectId));
    return response.data;
};

export const getAvailableSprints = async (projectId: string): Promise<SprintOption[]> => {
    const response = await apiClient.get<SprintOption[]>(API_ENDPOINTS.sprints.available(projectId));
    return response.data;
};

export const createTask = async (payload: CreateTaskPayload): Promise<TaskDetailItem> => {
    const response = await apiClient.post<TaskDetailItem>(API_ENDPOINTS.tasks.create, payload);
    return response.data;
};

export const updateTask = async (taskId: string, payload: UpdateTaskPayload): Promise<TaskDetailItem> => {
    const response = await apiClient.put<TaskDetailItem>(API_ENDPOINTS.tasks.update(taskId), payload);
    return response.data;
};

export const deleteTask = async (taskId: string): Promise<void> => {
    await apiClient.delete(API_ENDPOINTS.tasks.delete(taskId));
};
