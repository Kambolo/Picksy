import type { CategoryType } from "../types/CategoryDetails";
import { apiRequest } from "./apiRequest";

export function getCategories(
  page?: number,
  size?: number,
  sortBy?: string,
  ascending?: boolean,
  pattern?: string
) {
  const params = new URLSearchParams();

  if (page) params.append("page", String(page - 1));
  if (size) params.append("size", String(size));
  if (sortBy) params.append("sortBy", sortBy);
  if (ascending !== undefined) params.append("ascending", String(ascending));
  if (pattern) params.append("pattern", pattern);

  const endpoint = pattern
    ? `api/category/public/search?${params.toString()}`
    : `api/category/public?${params.toString()}`;

  return apiRequest(endpoint, "GET");
}

export function getCategory(id: number) {
  return apiRequest(`api/category/public/${id}/details`);
}

export function increaseViews(id: number) {
  return apiRequest(`api/category/public/${id}/increase`, "PATCH");
}

export function getCategoryOptions(id: number) {
  return apiRequest(`api/option/public/${id}`, "GET");
}

export function createCategory(
  name: string,
  type: CategoryType,
  description: string,
  isPublic: boolean
) {
  return apiRequest(`api/category/secure`, "POST", true, false, {
    name,
    type,
    description,
    isPublic,
  });
}

export function setCategoryImage(catId: number, file: File) {
  const formData = new FormData();
  formData.append("image", file);

  return apiRequest(
    `api/category/secure/image/${catId}`,
    "PATCH",
    true,
    true,
    formData
  );
}

export function createOption(name: string, catId: number) {
  return apiRequest(`api/option/secure`, "POST", true, false, {
    name,
    categoryId: catId,
  });
}

export function addOptionImage(file: File, optId: number) {
  const formData = new FormData();
  formData.append("image", file);

  return apiRequest(
    `api/option/secure/image/${optId}`,
    "PATCH",
    true,
    true,
    formData
  );
}

export function updateCategory(
  catId: number,
  name?: string,
  type?: CategoryType,
  description?: string,
  isPublic?: boolean
) {
  return apiRequest(`api/category/secure/${catId}`, "PATCH", true, false, {
    name,
    type,
    description,
    isPublic,
  });
}

export function deleteOption(optionId: number) {
  return apiRequest(`api/option/secure/${optionId}`, "DELETE", true);
}

export function updateOption(optionId: number, title: string) {
  return apiRequest(
    `api/option/secure/${optionId}`,
    "PATCH",
    true,
    false,
    title
  );
}

export function getUserCategories(
  userId: number,
  page?: number,
  size?: number,
  sortBy?: string,
  ascending?: boolean,
  pattern?: string
) {
  const params = new URLSearchParams();

  if (page) params.append("page", String(page - 1));
  if (size) params.append("size", String(size));
  if (sortBy) params.append("sortBy", sortBy);
  if (ascending !== undefined) params.append("ascending", String(ascending));
  if (pattern) params.append("pattern", pattern);

  const base = pattern
    ? `api/category/secure/search/author/${userId}?`
    : `api/category/secure/author/${userId}?`;

  return apiRequest(`${base}${params.toString()}`, "GET", true);
}

export function getCategoryWithOptions(catId: number) {
  return apiRequest(`api/category/public/${catId}/options`);
}

export function getPublicUserCategories(userId: number) {
  return apiRequest(`api/category/public/author/${userId}`);
}

export function deleteCategory(catId: number) {
  return apiRequest(`api/category/secure/${catId}`, "DELETE", true);
}

export function deleteOptionImage(optId: number) {
  return apiRequest(`api/option/secure/image/${optId}`, "DELETE", true);
}
