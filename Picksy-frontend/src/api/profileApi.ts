import { apiRequest } from "./apiRequest";

export function getProfile(id: number) {
  return apiRequest(`api/profile/public/${id}`, "GET", false);
}

export function setAvatar(file: File) {
  const formData = new FormData();
  formData.append("image", file);

  return apiRequest(`api/profile/secure/avatar`, "PATCH", true, true, formData);
}

export function changeProfileBio(bio: string) {
  return apiRequest(`api/profile/secure/bio`, "PATCH", true, false, bio);
}

export function changeUserDetails(id: number, username: string, email: string) {
  return apiRequest(`auth/account/secure/details`, "PATCH", true, false, {
    id,
    username,
    email,
  });
}

export function getProfiles(ids: number[]) {
  const query = ids.map((id) => `ids=${id}`).join("&");
  return apiRequest(`api/profile/public?${query}`);
}


