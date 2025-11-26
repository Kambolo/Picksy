import { useState, useEffect } from "react";
import { getProfile, setAvatar } from "../api/profileApi";
import { getUser } from "../api/authApi";
import type { UserProfile } from "../types/UserProfile";

export const useProfileData = (userId: number | null) => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch profile data
  useEffect(() => {
    const fetchProfile = async (): Promise<void> => {
      try {
        setIsLoading(true);

        if (!userId) {
          setError("Błąd podczas pobierania danych, zaloguj się ponownie.");
          return;
        }

        const responseProfile = await getProfile(userId);
        const responseUser = await getUser(userId);

        if (responseProfile.status === 200 && responseUser.status === 200) {
          setProfile({
            id: userId,
            username: responseUser.result?.username ?? "",
            email: responseUser.result?.email ?? "",
            avatarUrl: responseProfile.result?.avatarUrl ?? "",
            bio: responseProfile.result?.bio ?? "",
            isBlocked: responseUser.result.isBlocked,
          });
        } else {
          if (responseProfile.error) setError(responseProfile.error);
          if (responseUser.error) setError(responseUser.error);
        }
      } catch (err) {
        setError("Błąd podczas ładowania danych.");
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    if (userId) {
      fetchProfile();
    }
  }, [userId]);

  // Handle avatar upload
  const handleAvatarUpload = async (file: File): Promise<void> => {
    if (!userId) return;

    try {
      const response = await setAvatar(file);
      if (response.status === 200) {
        console.log(response.result);
        setProfile((prev) =>
          prev ? { ...prev, avatarUrl: response.result.message } : prev
        );
      } else if (response.error) {
        setError(response.error);
      }
    } catch (err) {
      setError("Błąd podczas zmiany awatara");
      console.error(err);
    }
  };

  return {
    profile,
    setProfile,
    isLoading,
    setIsLoading,
    error,
    setError,
    handleAvatarUpload,
  };
};
