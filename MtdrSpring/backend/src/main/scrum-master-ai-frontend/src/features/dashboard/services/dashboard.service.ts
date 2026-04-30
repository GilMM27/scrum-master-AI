import { apiClient } from "../../../services/Api";
import { API_ENDPOINTS } from "../../../services/Endpoints";
import type {
  ProjectAnalyticsData,
  SprintAnalyticsData,
} from "../types/dashboard.types";

export async function getProjectAnalytics(
  projectId: string,
): Promise<ProjectAnalyticsData> {
  const response = await apiClient.get<ProjectAnalyticsData>(
    API_ENDPOINTS.analytics.byProject(projectId),
  );
  return response.data;
}

export async function getSprintAnalytics(
  sprintId: string,
): Promise<SprintAnalyticsData> {
  const response = await apiClient.get<SprintAnalyticsData>(
    API_ENDPOINTS.analytics.bySprint(sprintId),
  );
  return response.data;
}
