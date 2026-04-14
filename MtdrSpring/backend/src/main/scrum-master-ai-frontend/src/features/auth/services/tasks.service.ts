import { apiClient } from "../../../services/Api";
import { API_ENDPOINTS } from "../../../services/Endpoints";

export const getTasks = async () => {
  const res = await apiClient.get(API_ENDPOINTS.TASKS.GET_ALL);
  return res.data;
};

export const getTasksByStatus = async (status: string) => {
  const res = await apiClient.get(`${API_ENDPOINTS.TASKS.GET_BY_STATUS}/${status}`);
  return res.data;
};

export const createTask = async (task: any) => {
  const res = await apiClient.post(API_ENDPOINTS.TASKS.CREATE, task);
  return res.data;
};

export const updateTaskStatus = async (id: number, status: string) => {
  const res = await apiClient.put(`${API_ENDPOINTS.TASKS.UPDATE_STATUS}/${id}`, {
    status,
  });
  return res.data;
};

export const deleteTask = async (id: number) => {
  const res = await apiClient.delete(`${API_ENDPOINTS.TASKS.DELETE}/${id}`);
  return res.data;
};