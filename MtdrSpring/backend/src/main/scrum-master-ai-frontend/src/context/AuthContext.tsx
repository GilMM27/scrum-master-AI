import { createContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { AuthState, LoginRequest } from '../types/Auth.types';
import type { User } from '../types/User.types';
import loginRequest from '../features/auth/services/auth.service';

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
  const [authState, setAuthState] = useState<AuthState>(initialState);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const userJson = localStorage.getItem('authUser');

    if (token && userJson) {
      const user: User = JSON.parse(userJson);
      setAuthState({ isAuthenticated: true, token, user });
    }
  }, []);
  
  const login = async (credentials: LoginRequest) => {
    const response = await loginRequest(credentials);

    const user: User = {
      username: response.username,
      email: response.email,
      role: response.role,
    };

    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('authUser', JSON.stringify(user));

    setAuthState({ isAuthenticated: true, token: response.accessToken, user });

    return user;
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('authUser');
    setAuthState(initialState);
  };

  const value = useMemo(() => ({ ...authState, login, logout }), [authState]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;