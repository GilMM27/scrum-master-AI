import type { LoginRequest, LoginResponse } from "../../../types/Auth.types";

const loginRequest = async (credentials: LoginRequest): Promise<LoginResponse> => {
    await new Promise((resolve) => setTimeout(resolve, 1000));

    if (credentials.username === 'admin' && credentials.password === 'Admin123!') {
        return {
            accessToken: 'fake-admin-token',
            username: 'admin',
            email: 'admin@oracle.com',
            role: 'ADMIN',
        };
    }

    if (credentials.username === 'manager' && credentials.password === 'Manager123!') {
        return {
            accessToken: 'fake-manager-token',
            username: 'manager',
            email: 'manager@oracle.com',
            role: 'MANAGER',
        };
    }

    if (credentials.username === 'developer' && credentials.password === 'Developer123!') {
        return {
            accessToken: 'fake-developer-token',
            username: 'developer',
            email: 'developer@oracle.com',
            role: 'DEVELOPER',
        };
    }

    throw new Error('Nombre de usuario o contraseña incorrectos');
}

export default loginRequest;