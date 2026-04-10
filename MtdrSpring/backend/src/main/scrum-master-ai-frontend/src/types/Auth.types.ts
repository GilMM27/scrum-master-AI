import type { User } from './User.types';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  username: string;
  email: string;
  role: 'DEVELOPER' | 'MANAGER' | 'ADMIN';
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
}