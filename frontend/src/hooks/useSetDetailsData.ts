import { useEffect, useState } from "react";
import { getUser } from "../api/authApi";
import { getSetById } from "../api/categoryApi";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import type { SetInfo } from "../types/Set";

export type SetDetails = SetInfo & {
  categories: CategoryCardProps[] | undefined;
};

export const useSetDetailsData = (id: number, isCreatingCategory: boolean) => {
  const [set, setSet] = useState<SetDetails | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    const fetchSet = async () => {
      setIsLoading(true);

      const response = await getSetById(id);
      if (response.status !== 200) {
        setIsLoading(false);
        setError("Wystąpił błąd przy pobieraniu danych: " + response.error);
      }

      const authorResponse = await getUser(response.result.authorId);
      if (authorResponse.status !== 200) {
        setIsLoading(false);
        setError(
          "Wystąpił błąd przy pobieraniu danych o autorze: " + response.error
        );
      }

      const categories: CategoryCardProps[] = await Promise.all(
        response.result.categories.map(async (cat: any) => {
          return {
            id: cat.id,
            img: cat.photoURL,
            title: cat.name,
            author: "",
            authorId: -2,
            description: cat.description,
            type: cat.type,
            isPublic: cat.isPublic,
            showIsPublic: false,
            views: cat.views,
          };
        })
      );

      const details: SetDetails = {
        id: response.result.id,
        title: response.result.name,
        author: authorResponse.result.username,
        authorId: response.result.authorId,
        isPublic: response.result.isPublic,
        views: response.result.views,
        showIsPublic: false,
        categories,
      };

      setSet(details);
      setIsLoading(false);
    };
    fetchSet();
  }, [id, isCreatingCategory]);

  return {
    set,
    isLoading,
    error,
    setError,
  };
};
