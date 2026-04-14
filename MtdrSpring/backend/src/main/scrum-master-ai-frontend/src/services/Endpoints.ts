export const API_ENDPOINTS = {
    auth: {
        login: '/auth/login',
        register: '/auth/register',
    },
    users: {
        base: '/api/users',
        byId: (userId: string) => `/api/users/${userId}`,
        updateRole: (userId: string) => `/api/users/${userId}/role`,
        updateAuthorization: (userId: string) => `/api/users/${userId}/authorization`,
        createManagedUser: '/api/users',
    },
};