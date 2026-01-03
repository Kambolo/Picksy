import { useEffect, useState } from "react";
import { getUser } from "../api/authApi";
import {
  getAllPublicSets,
  getAllSetsByAuthorId,
  increaseSetViews,
} from "../api/categoryApi";
import type { SetCardProps } from "../components/SetCard/SetCard";
import type { SetInfo } from "../types/Set";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";

export const useSetData = (
  currentPage: number,
  sortByForApi: string,
  ascending: boolean,
  debouncedSearchValue: string,
  userId: number | undefined,
  setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
  setTotalPages: React.Dispatch<React.SetStateAction<number>>
) => {
  const PAGE_SIZE = 10;
  const [availableSets, setAvailableSets] = useState<SetCardProps[]>([]);
  const [totalSets, setTotalSets] = useState(0);
  const [error, setError] = useState("");

  // Fetch author name by ID
  const fetchAuthorName = async (authorID: number | null): Promise<string> => {
    if (authorID === null) return "Picksy";

    const response = await getUser(authorID);
    if (response.status !== 200) {
      console.error("Failed to fetch author:", response.error);
      return "Picksy";
    }

    return response.result.username;
  };

  const transformSet = async (response: any): Promise<SetCardProps> => {
    const author = await fetchAuthorName(response.authorId);

    const categories: CategoryCardProps[] = await Promise.all(
      response.categories.map(async (cat) => {
        return {
          id: cat.id,
          img: cat.photoURL,
          title: cat.name,
          author: author,
          authorId: response.authorId,
          description: cat.description,
          type: cat.type,
          views: cat.views,
          isPublic: cat.isPublic,
          showIsPublic: false,
        };
      })
    );

    const setInfo: SetInfo = {
      id: response.id,
      title: response.name,
      author,
      authorId: response.authorId || -1,
      isPublic: response.isPublic,
      views: response.views,
      showIsPublic: userId ? true : false,
      categories,
    };

    return { ...setInfo, categoryCount: categories.length };
  };

  const fetchSets = async () => {
    setIsLoading(true);
    setError("");

    try {
      let response = null;

      if (!userId) {
        response = await getAllPublicSets(
          currentPage,
          PAGE_SIZE,
          sortByForApi,
          ascending,
          debouncedSearchValue
        );
      } else {
        response = await getAllSetsByAuthorId(
          userId,
          currentPage,
          PAGE_SIZE,
          sortByForApi,
          ascending,
          debouncedSearchValue
        );
      }

      if (response.status !== 200) {
        setError(response.error);
        return;
      }

      const fetchedSets = response.result.content;

      const transformedSets = await Promise.all(fetchedSets.map(transformSet));

      setAvailableSets(transformedSets);

      // Update pagination info
      const pageInfo = response.result.page;
      setTotalSets(pageInfo.totalElements);
      setTotalPages(pageInfo.totalPages);
    } catch (err) {
      setError("An unexpected error occurred while fetching categories.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  // Increase views for a set
  const handleIncreaseViews = async (id: number): Promise<void> => {
    try {
      const result = await increaseSetViews(id);
      if (result.status !== 204) {
        console.error("Failed to increase views:", result.error);
      }
    } catch (err) {
      console.error("Error increasing views:", err);
    }
  };

  useEffect(() => {
    fetchSets();
  }, [currentPage, sortByForApi, ascending, debouncedSearchValue, userId]);

  return {
    availableSets,
    totalSets,
    error,
    setError,
    handleIncreaseViews,
    fetchAuthorName,
  };
};
