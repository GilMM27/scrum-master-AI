/* eslint-disable react-refresh/only-export-components */
import { createContext, useMemo, useState, type ReactNode } from "react";
import type { AuthState, LoginRequest } from "../types/Auth.types";
import type { User } from "../types/User.types";
import loginRequest from "../features/auth/services/auth.service";
import { clearAuthStorage, getAccessToken, getAuthUser, setAccessToken, setAuthUser } from "../utils/Storage";

interface AuthContextValue extends AuthState {
  login: (credentials: LoginRequest) => Promise<User>;
  logout: () => void;
}

const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  token: null,
};

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

const AuthProvider = ({ children }: AuthProviderProps) => {
  const [authState, setAuthState] = useState<AuthState>(() => {
    const token = getAccessToken();
    const user = getAuthUser<User>();
    if (token && user) {
      return { isAuthenticated: true, token, user };
    }
    return initialState;
  });

  const login = async (credentials: LoginRequest) => {
    const response = await loginRequest(credentials);

    const user: User = {
      username: response.username,
      email: response.email,
      role: response.role,
    };

    setAccessToken(response.token);
    setAuthUser(user);
    setAuthState({ isAuthenticated: true, token: response.token, user });

    return user;
  };

  const logout = () => {
    clearAuthStorage();
    setAuthState(initialState);
  };

  const value = useMemo(() => ({ ...authState, login, logout }), [authState]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthProvider;
