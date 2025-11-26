import { useState } from "react";
import { changeProfileBio, changeUserDetails } from "../api/profileApi";
import type { UserProfile } from "../types/UserProfile";
import { useUser } from "../context/userContext";

const BIO_LIMIT = 150;
const GENERAL_LIMIT = 40;

export const useProfileEdit = (
  profile: UserProfile | null,
  setProfile: React.Dispatch<React.SetStateAction<UserProfile | null>>,
  setIsLoading: React.Dispatch<React.SetStateAction<boolean>>
) => {
  const [backupProfile, setBackupProfile] = useState<UserProfile | null>(null);
  const [editFields, setEditFields] = useState<boolean>(false);
  const [editError, setEditError] = useState<string | null>(null);
  const { setUser } = useUser();

  // Validate field length
  const validateField = (name: string, value: string): boolean => {
    const limit = name === "bio" ? BIO_LIMIT : GENERAL_LIMIT;

    if (value.length > limit) {
      setEditError(`${name}: maksymalna długość to ${limit}`);
      return false;
    }

    setEditError(null);
    return true;
  };

  // Handle input change
  const handleEditProfile = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ): void => {
    const { name, value } = event.target;

    if (!validateField(name, value)) {
      return;
    }

    setProfile((prev) => (prev ? { ...prev, [name]: value } : prev));

    if (name === "username")
      setUser((prev) => (prev ? { ...prev, username: value } : prev));
    else if (name === "email")
      setUser((prev) => (prev ? { ...prev, username: value } : prev));
  };

  // Enable edit mode
  const handleEditClick = (): void => {
    if (profile) {
      setBackupProfile({ ...profile });
    }
    setEditFields(true);
    setEditError(null);
  };

  // Cancel edit
  const handleCancelEdit = (): void => {
    if (backupProfile) {
      setProfile(backupProfile);
    }
    setEditFields(false);
    setEditError(null);
    setBackupProfile(null);
  };

  // Save changes to API
  const handleSave = async (): Promise<void> => {
    if (!profile) return;

    setIsLoading(true);
    setEditError(null);

    try {
      // Update bio if not empty
      if (profile.bio !== "") {
        const bioResponse = await changeProfileBio(profile.bio);
        if (bioResponse.status !== 200) {
          setEditError(bioResponse.error ?? "Błąd podczas zmiany bio");
          if (backupProfile) setProfile(backupProfile);
          return;
        }
      }

      // Update user details
      const userResponse = await changeUserDetails(
        profile.id,
        profile.username,
        profile.email
      );

      if (userResponse.status !== 200) {
        setEditError(
          userResponse.error ?? "Błąd podczas zmiany danych użytkownika"
        );
        if (backupProfile) setProfile(backupProfile);
        return;
      }

      // Success
      setEditFields(false);
      setBackupProfile(null);
    } catch (err) {
      console.error(err);
      setEditError("Nieoczekiwany błąd");
      if (backupProfile) setProfile(backupProfile);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    editFields,
    editError,
    handleEditProfile,
    handleEditClick,
    handleCancelEdit,
    handleSave,
  };
};
