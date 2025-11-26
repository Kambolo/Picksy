import { getProfile } from "../api/profileApi";

export const DEFAULT_PHOTO_URL =
  "https://res.cloudinary.com/dctiucda1/image/upload/v1760449592/default.png";

async function fetchPhotoUrl(id: number | null): Promise<string> {
  if (!id || id <= 0) return DEFAULT_PHOTO_URL;
  const response = await getProfile(id);
  return response.status === 200
    ? response.result.avatarUrl
    : DEFAULT_PHOTO_URL;
}

export default fetchPhotoUrl;
