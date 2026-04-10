import type { UserRole } from './Role.types';

export interface User {
    id: string;
    username: string;
    email: string;
    role: UserRole;
}