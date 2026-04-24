export interface ProjectSelectorItem {
    projectId: string;
    name: string;
}

export interface CreateProjectRequest {
    name: string;
    description: string;
}

export interface CreateProjectResponse {
    projectId: string;
    name: string;
    description: string;
}
