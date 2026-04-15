import axios from "axios";
import { apiClient } from "../../../services/Api";
import type { CreateManagedUserRequest, UpdateUserAuthorizationRequest, UpdateUserRoleRequest, UserDetail, UserSummary } from "../types/users.types";
import { API_ENDPOINTS } from "../../../services/Endpoints";

const extractErrorMessage = (error: unknown, fallback: string) => {
    if (axios.isAxiosError(error)) {
        return (
            error.response?.data?.message ||
            error.message ||
            fallback
        );
    }
    return fallback;
};

export async function getAllUsers(): Promise<UserSummary[]> {
    try {
        const response = await apiClient.get<UserSummary[]>(API_ENDPOINTS.users.base);
        return response.data;
    } catch (error) {
        throw new Error(extractErrorMessage(error, 'No fue posible obtener la lista de usuarios.'));
    }
}

export async function getUserById(userId: string): Promise<UserDetail> {
    try {
        const response = await apiClient.get<UserDetail>(`${API_ENDPOINTS.users.byId(userId)}`);
        return response.data;
    } catch (error) {
        throw new Error(extractErrorMessage(error, 'No fue posible obtener los detalles del usuario.'));
    }
}


export async function updateUserRole(
  userId: string,
  payload: UpdateUserRoleRequest,
): Promise<UserDetail> {
  try {
    const response = await apiClient.patch<UserDetail>(
      `${API_ENDPOINTS.users.updateRole(userId)}`,
      payload,
    );
    return response.data;
  } catch (error) {
    throw new Error(extractErrorMessage(error, 'No fue posible actualizar el rol del usuario.'));
  }
}

export async function updateUserAuthorization(
  userId: string,
  payload: UpdateUserAuthorizationRequest,
): Promise<UserDetail> {
  try {
    const response = await apiClient.patch<UserDetail>(
      `${API_ENDPOINTS.users.updateAuthorization(userId)}`,
      payload,
    );
    return response.data;
  } catch (error) {
    throw new Error(
      extractErrorMessage(error, 'No fue posible actualizar el estado del usuario.'),
    );
  }
}

export async function createManagedUser(payload: CreateManagedUserRequest): Promise<UserDetail> {
    try {
        const response = await apiClient.post<UserDetail>(API_ENDPOINTS.users.createManagedUser, payload);
        return response.data;
    } catch (error) {
        throw new Error(extractErrorMessage(error, 'No fue posible crear el usuario.'));
    }
}