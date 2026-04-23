import { apiClient } from '../../../services/Api';
import { API_ENDPOINTS } from '../../../services/Endpoints';
import type { TaskDetailItem, TaskItem } from '../types/tasks.types';

export const getProjectTasks = async (projectId: string): Promise<TaskItem[]> => {
    const response = await apiClient.get<TaskItem[]>(API_ENDPOINTS.tasks.byProject(projectId));
    return response.data;
};

export const getTaskDetails = async (taskId: string): Promise<TaskDetailItem> => {
    const response = await apiClient.get<TaskDetailItem>(API_ENDPOINTS.tasks.byId(taskId));
    return response.data;
};
