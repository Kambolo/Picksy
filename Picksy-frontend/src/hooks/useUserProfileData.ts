import { useEffect, useState } from "react";
import { getPublicUserCategories, getUserCategories } from "../api/categoryApi";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import { useUser } from "../context/userContext";
import type { CategoryInfo } from "../types/CategoryInfo";
import { useProfileData } from "./useProfileData";

const useUserProfileData = (
  userId: number,
  setIsBanned: (isBanned: boolean) => void
) => {
  const [userCategories, setUserCategories] = useState<CategoryCardProps[]>([]);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);
  const { user } = useUser();

  const {
    profile,
    isLoading: isLoadingProfile,
    error,
    setError,
  } = useProfileData(userId);

  useEffect(() => {
    setIsBanned(profile?.isBlocked || false);
  }, [profile]);

  const transformCategory = (category: CategoryInfo): CategoryCardProps => {
    return {
      id: category.id,
      title: category.name,
      author: profile?.username || "",
      authorId: category.authorID || -1,
      type: category.type,
      description: category.description ?? "-",
      img: category.photoURL,
      isPublic: category.isPublic,
      views: category.views,
      showIsPublic: userId ? true : false,
    };
  };

  useEffect(() => {
    const fetchUserCategories = async () => {
      setIsLoadingCategories(true);

      let response = null;
      if (user?.role === "ADMIN") response = await getUserCategories(userId);
      else response = await getPublicUserCategories(userId);

      if (response.status !== 200) {
        setError("Wystapił nieoczekiwany bład podczas pobierania kategorii");
        setIsLoadingCategories(false);
        return;
      }

      const fetchedCategories: CategoryInfo[] = response.result.content;

      const transformedCategories = fetchedCategories.map(transformCategory);

      setUserCategories(transformedCategories);
      setIsLoadingCategories(false);
    };

    fetchUserCategories();
  }, [userId]);

  return {
    profile,
    isLoadingProfile,
    userCategories,
    isLoadingCategories,
    setError,
    error,
  };
};

export default useUserProfileData;
