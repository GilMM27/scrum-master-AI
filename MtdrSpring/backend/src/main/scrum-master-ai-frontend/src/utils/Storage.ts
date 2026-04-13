const ACCESS_TOKEN_KEY = 'accessToken';
const AUTH_USER_KEY = 'authUser';

export function setAccessToken(token: string) {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function removeAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

export function setAuthUser(user: unknown) {
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
}

export function getAuthUser<T>(): T | null {
  const userJson = localStorage.getItem(AUTH_USER_KEY);
  if (!userJson) return null;

  try {
    return JSON.parse(userJson) as T;
  } catch {
    return null;
  }
}

export function removeAuthUser() {
  localStorage.removeItem(AUTH_USER_KEY);
}

export function clearAuthStorage() {
  removeAccessToken();
  removeAuthUser();
}