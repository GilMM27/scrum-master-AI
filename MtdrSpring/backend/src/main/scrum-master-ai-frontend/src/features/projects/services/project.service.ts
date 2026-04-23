import { apiClient } from '../../../services/Api';
import { API_ENDPOINTS } from '../../../services/Endpoints';
import type { CreateProjectRequest, CreateProjectResponse, ProjectSelectorItem } from '../../../types/Project.types';

export const getMyProjectsSelector = async (): Promise<ProjectSelectorItem[]> => {
    const response = await apiClient.get<ProjectSelectorItem[]>(API_ENDPOINTS.projects.mySelector);
    return response.data;
};

export const createProject = async (data: CreateProjectRequest): Promise<CreateProjectResponse> => {
    const response = await apiClient.post<CreateProjectResponse>(API_ENDPOINTS.projects.base, data);
    return response.data;
};
