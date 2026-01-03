import React, { type Dispatch, type SetStateAction } from "react";
import { useCategorySelection } from "../../hooks/useCategorySelection";
import { useCategoryData } from "../../hooks/useCategoryData";
import { useNavigate } from "react-router-dom";
import CategoryList from "../CategoryList/CategoryList";
import { useUser } from "../../context/userContext";

export type ViewProps = {
  currentPage: number;
  sortByForApi: string;
  ascending: boolean;
  debouncedSearchValue: string;
  userId: number;
  isLoading: boolean;
  setIsLoading: Dispatch<SetStateAction<boolean>>;
  setTotalPages: Dispatch<SetStateAction<number>>;
};

const CategoryView: React.FC<ViewProps> = ({
  currentPage,
  sortByForApi,
  ascending,
  debouncedSearchValue,
  userId,
  isLoading,
  setIsLoading,
  setTotalPages,
}) => {
  const navigate = useNavigate();
  const user = useUser();
  // Category data from API
  const { availableCategories, error, setError, handleIncreaseViews } =
    useCategoryData(
      currentPage,
      sortByForApi,
      ascending,
      debouncedSearchValue,
      userId,
      setIsLoading,
      setTotalPages
    );

  // Category selection for room creation
  const {
    selectedCategories,
    isAddCategoryOpen,
    handleCategoryCardCheck,
    handleSaveCategories,
  } = useCategorySelection(availableCategories, setError);

  // Handle category card click - navigate to details
  const handleCardClick = async (id: number) => {
    try {
      const selectedCategory = await handleIncreaseViews(id);

      if (!selectedCategory) {
        setError("Failed to load category details.");
        return;
      }

      navigate(`/category/${id}`);
    } catch (err) {
      console.error("Error handling card click:", err);
      setError("An error occurred while opening the category.");
    }
  };
  return (
    <>
      {isAddCategoryOpen && user && (
        <div className="category-save">
          <button onClick={handleSaveCategories}>
            Zapisz wybrane kategorie ({selectedCategories.length})
          </button>
        </div>
      )}
      {error && <div className="error-message-category">{error}</div>}
      <CategoryList
        categoryListProps={availableCategories}
        isLoading={isLoading}
        onCardClick={handleCardClick}
        onChange={handleCategoryCardCheck}
        selectedCategories={selectedCategories}
      />
    </>
  );
};

export default CategoryView;
