import axios from "axios";
import { apiClient } from "../../../services/Api";
import { API_ENDPOINTS } from "../../../services/Endpoints";
import type { DeveloperSummary, ProjectSummary } from "../types/adminProjects.types";

const extractErrorMessage = (error: unknown, fallback: string): string => {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.message || error.message || fallback;
  }
  return fallback;
};

export async function getAllProjects(): Promise<ProjectSummary[]> {
  try {
    const response = await apiClient.get<ProjectSummary[]>(
      API_ENDPOINTS.projects.all,
    );
    return response.data;
  } catch (error) {
    throw new Error(
      extractErrorMessage(error, "No fue posible obtener los proyectos."),
    );
  }
}

export async function getAllDevelopers(): Promise<DeveloperSummary[]> {
  try {
    const response = await apiClient.get<DeveloperSummary[]>(
      API_ENDPOINTS.users.developers,
    );
    return response.data;
  } catch (error) {
    throw new Error(
      extractErrorMessage(error, "No fue posible obtener los desarrolladores."),
    );
  }
}

export async function getDeveloperProjects(
  userId: string,
): Promise<ProjectSummary[]> {
  try {
    const response = await apiClient.get<ProjectSummary[]>(
      API_ENDPOINTS.users.projectsByUser(userId),
    );
    return response.data;
  } catch (error) {
    throw new Error(
      extractErrorMessage(
        error,
        "No fue posible obtener los proyectos del desarrollador.",
      ),
    );
  }
}

export async function linkDeveloperToProject(
  projectId: string,
  userId: string,
): Promise<void> {
  try {
    await apiClient.post(API_ENDPOINTS.projects.addMember(projectId, userId));
  } catch (error) {
    throw new Error(
      extractErrorMessage(
        error,
        "No fue posible vincular al desarrollador con el proyecto.",
      ),
    );
  }
}

export async function unlinkDeveloperFromProject(
  projectId: string,
  userId: string,
): Promise<void> {
  try {
    await apiClient.delete(
      API_ENDPOINTS.projects.removeMember(projectId, userId),
    );
  } catch (error) {
    throw new Error(
      extractErrorMessage(
        error,
        "No fue posible desvincular al desarrollador del proyecto.",
      ),
    );
  }
}
