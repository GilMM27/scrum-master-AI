import axios from "axios";
import { apiClient } from "../../../services/Api";
import { API_ENDPOINTS } from "../../../services/Endpoints";
import type { LoginRequest, LoginResponse } from "../../../types/Auth.types";

export class InactiveAccountError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "InactiveAccountError";
  }
}

const loginRequest = async (
  credentials: LoginRequest,
): Promise<LoginResponse> => {
  try {
    console.log("[Auth] Connecting to backend...", API_ENDPOINTS.auth.login);
    const response = await apiClient.post<LoginResponse>(
      API_ENDPOINTS.auth.login,
      credentials,
    );
    console.log("[Auth] Connection succeeded. Status:", response.status);
    console.log("[Auth] Backend response:", response.data);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response?.data?.errorCode === "ACCOUNT_INACTIVE") {
        throw new InactiveAccountError(
          error.response.data.message ||
            "Tu cuenta se encuentra inactiva. Contacta a un administrador para reactivar el acceso.",
        );
      }
      const msg =
        error.response?.data?.message ||
        error.response?.data?.error ||
        "Contraseña o usuario incorrectos, inténtalo de nuevo.";
      throw new Error(msg);
    }
    throw new Error(
      "Ocurrió un error inesperado al iniciar sesión. Por favor, inténtalo de nuevo más tarde.",
    );
  }
};

export default loginRequest;
