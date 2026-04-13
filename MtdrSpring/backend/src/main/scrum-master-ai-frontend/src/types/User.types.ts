import type { UserRole } from './Role.types';

export interface User {
    username: string;
    email: string;
    role: UserRole;
}