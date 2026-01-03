import { useState, useEffect } from "react";
import {
  getCategories,
  getUserCategories,
  increaseViews,
} from "../api/categoryApi";
import { getUser } from "../api/authApi";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import type { CategoryInfo } from "../types/CategoryInfo";

const PAGE_SIZE = 10;

export const useCategoryData = (
  currentPage: number,
  sortByForApi: string,
  ascending: boolean,
  debouncedSearchValue: string,
  userId: number | undefined,
  setIsLoading: React.Dispatch<React.SetStateAction<boolean>>,
  setTotalPages: React.Dispatch<React.SetStateAction<number>>
) => {
  const [availableCategories, setAvailableCategories] = useState<
    CategoryCardProps[]
  >([]);
  const [categoriesFromApi, setCategoriesFromApi] = useState<CategoryInfo[]>(
    []
  );
  const [totalCategories, setTotalCategories] = useState(0);
  const [error, setError] = useState("");

  // Fetch author name by ID
  const fetchAuthorName = async (authorID: number): Promise<string> => {
    if (authorID === -1) return "Picksy";

    const response = await getUser(authorID);
    if (response.status !== 200) {
      console.error("Failed to fetch author:", response.error);
      return "Picksy";
    }

    return response.result.username;
  };

  // Transform API category to CategoryCardProps
  const transformCategory = async (
    category: CategoryInfo
  ): Promise<CategoryCardProps> => {
    const author = await fetchAuthorName(category.authorID);

    return {
      id: category.id,
      title: category.name,
      author,
      authorId: category.authorID || -1,
      type: category.type,
      description: category.description ?? "-",
      img: category.photoURL,
      isPublic: category.isPublic,
      views: category.views,
      showIsPublic: userId ? true : false,
    };
  };

  // Fetch categories from API
  const fetchCategories = async () => {
    setIsLoading(true);
    setError("");

    try {
      let response = null;

      if (!userId) {
        response = await getCategories(
          currentPage,
          PAGE_SIZE,
          sortByForApi,
          ascending,
          debouncedSearchValue
        );
      } else {
        response = await getUserCategories(
          userId,
          currentPage,
          PAGE_SIZE,
          sortByForApi,
          ascending,
          debouncedSearchValue
        );
      }

      if (response.status !== 200) {
        console.log({
          currentPage,
          PAGE_SIZE,
          sortByForApi,
          ascending,
          debouncedSearchValue,
        });
        setError(response.error);
        return;
      }

      const fetchedCategories: CategoryInfo[] = response.result.content;
      setCategoriesFromApi(fetchedCategories);

      // Transform categories with author info
      const transformedCategories = await Promise.all(
        fetchedCategories.map(transformCategory)
      );

      setAvailableCategories(transformedCategories);

      // Update pagination info
      const pageInfo = response.result.page;
      setTotalCategories(pageInfo.totalElements);
      setTotalPages(pageInfo.totalPages);
    } catch (err) {
      setError("An unexpected error occurred while fetching categories.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  // Increase views for a category
  const handleIncreaseViews = async (
    id: number
  ): Promise<CategoryInfo | null> => {
    try {
      const result = await increaseViews(id);
      if (result.status !== 200) {
        console.error("Failed to increase views:", result.error);
      }

      const selectedCategory = categoriesFromApi.find((cat) => cat.id === id);
      return selectedCategory || null;
    } catch (err) {
      console.error("Error increasing views:", err);
      return null;
    }
  };

  // Fetch categories when dependencies change
  useEffect(() => {
    fetchCategories();
  }, [currentPage, sortByForApi, ascending, debouncedSearchValue, userId]);

  return {
    availableCategories,
    categoriesFromApi,
    totalCategories,
    error,
    setError,
    handleIncreaseViews,
    fetchAuthorName,
  };
};
