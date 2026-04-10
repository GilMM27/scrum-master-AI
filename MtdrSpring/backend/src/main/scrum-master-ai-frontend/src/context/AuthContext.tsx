import { createContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { AuthState, LoginRequest } from '../types/Auth.types';
import type { User } from '../types/User.types';

interface AuthContextValue extends AuthState {
  login: (credentials: LoginRequest) => Promise<void>;
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
    const token = localStorage.getItem('token');
    const userJson = localStorage.getItem('user');

    if (token && userJson) {
      const user: User = JSON.parse(userJson);
      setAuthState({ isAuthenticated: true, user, token });
    }
  }, []);
  
  const login = async (credentials: LoginRequest) => {
    // Implement login logic here
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setAuthState(initialState);
  };

  const value = useMemo(() => ({ ...authState, login, logout }), [authState]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;