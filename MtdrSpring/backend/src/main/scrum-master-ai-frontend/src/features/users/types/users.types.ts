export type ManagedUserRole = 'ADMIN' | 'MANAGER' | 'DEVELOPER';
export type ManagedUserStatus = 'ACTIVE' | 'INACTIVE';

export interface UserSummary {
    userId: string;
    username: string;
    email: string;
    userRole: ManagedUserRole;
    accountStatus: ManagedUserStatus;
}

export interface UserDetail {
    userId: string;
    username: string;
    email: string;
    userRole: ManagedUserRole;
    accountStatus: ManagedUserStatus;
    telegramId: string | null;
    cellPhone: string | null;
    createdAt: string;
}

export interface CreateManagedUserRequest {
    username: string;
    email: string;
    password: string;
    cellPhone: string;
    userRole: ManagedUserRole;
}

export interface UpdateUserRoleRequest {
    userRole: ManagedUserRole;
}

export interface UpdateUserAuthorizationRequest {
    accountStatus: ManagedUserStatus;
}

export interface UsersFilterState {
    search: string;
    userRole: ManagedUserRole | '';
    accountStatus: ManagedUserStatus | '';
}