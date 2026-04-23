import { apiClient } from '../../../services/Api';
import { API_ENDPOINTS } from '../../../services/Endpoints';
import type { SprintOption, TaskAssignee, TaskDetailItem, TaskItem } from '../types/tasks.types';

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
