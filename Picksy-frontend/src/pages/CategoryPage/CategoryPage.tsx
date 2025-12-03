import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import CategoryList from "../../components/CategoryList/CategoryList";
import Navbar from "../../components/Navbar/Navbar";
import PageNavigation from "../../components/PageNavigation/PageNavigation";
import { useUser } from "../../context/userContext";
import { useCategoryData } from "../../hooks/useCategoryData";
import { useCategorySelection } from "../../hooks/useCategorySelection";
import { usePaginationAndSort } from "../../hooks/usePaginationAndSort";
import "./CategoryPage.css";

export const CategoryPage = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { userId } = useParams<{ userId: string }>();

  const [isLoading, setIsLoading] = useState(false);

  // Pagination, sorting and search
  const {
    currentPage,
    setCurrentPage,
    currentSortBy,
    sortByForApi,
    ascending,
    handleSortChange,
    searchValue,
    debouncedSearchValue,
    handleInputChange,
  } = usePaginationAndSort();

  // Category data from API
  const {
    availableCategories,
    totalPages,
    error,
    setError,
    handleIncreaseViews,
  } = useCategoryData(
    currentPage,
    sortByForApi,
    ascending,
    debouncedSearchValue,
    userId ? parseInt(userId ?? "1") : undefined,
    setIsLoading
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
    <div className="page-container">
      <Navbar />
      <PageNavigation
        value={searchValue}
        handleSearch={handleInputChange}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        sortBy={currentSortBy}
        onSortChange={handleSortChange}
      />
      <div className="category-page-container">
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
      </div>
    </div>
  );
};
