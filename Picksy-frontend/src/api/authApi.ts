import { apiRequest } from "./apiRequest";

export function login(email: string, password: string, rememberMe: boolean) {
  return apiRequest("auth/signin", "POST", true, false, {
    email,
    password,
    rememberMe,
  });
}

type SignUpProps = {
  username: string;
  email: string;
  password: string;
};

export function signup(request: SignUpProps) {
  return apiRequest("auth/signup", "POST", false, false, request);
}

export function sendCodeViaEmail(email: string) {
  return apiRequest("auth/code/generate", "POST", false, false, { email });
}

export function verifyCode(email: string, code: string) {
  return apiRequest("auth/code/check", "PUT", false, false, { email, code });
}

export function resetPassword(email: string, code: string, password: string) {
  return apiRequest("auth/password/reset", "PUT", false, false, {
    email,
    code,
    password,
  });
}

export function logout() {
  return apiRequest(
    "auth/logout",
    "POST",
    true // secure → wysyła cookies
  );
}

export function getUserFromCookies() {
  return apiRequest("auth/account/secure/me", "GET", true);
}

export function getUser(id: number) {
  return apiRequest(`auth/account/public/user/${id}`, "GET");
}

export function getUsers(
  page?: number,
  size?: number,
  ascending?: boolean,
  pattern?: string
) {
  const params = new URLSearchParams();

  if (page) params.append("page", String(page - 1));
  if (size) params.append("size", String(size));
  if (ascending !== undefined) params.append("ascending", String(ascending));
  if (pattern) params.append("pattern", pattern);

  const base = pattern ? `auth/account/public/search?` : `auth/account/public?`;

  return apiRequest(`${base}${params.toString()}`, "GET");
}

export function unbanUser(userId: number) {
  return apiRequest(`auth/account/secure/${userId}/unban`, "PATCH", true);
}

export function banUser(userId: number, banDays: number | null) {
  let banDate: string | null = null;

  if (banDays !== null) {
    const now = new Date();
    now.setDate(now.getDate() + banDays);
    banDate = now.toISOString().slice(0, 19);
  }

  const body = { userId, banDate };

  return apiRequest(`auth/account/secure/ban`, "PATCH", true, false, body);
}
