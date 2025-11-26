import { useNavigate } from "react-router-dom";
import { useRoomSetup } from "./useRoomSetup";

import { useUser } from "../context/userContext";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import { useCategoryUI } from "./useCategoryUIContext";

export const useCategorySelection = (
  availableCategories: CategoryCardProps[],
  setError: (error: string) => void
) => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { isAddCategoryOpen, setIsAddCategoryOpen } = useCategoryUI();
  const {
    addCategory,
    removeCategory,
    categories: selectedCategories,
  } = useRoomSetup();

  // Handle checkbox change
  const handleCategoryCardCheck = (
    id: number,
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const selectedCategory = availableCategories.find((cat) => cat.id === id);

    if (!selectedCategory) return;

    if (e.target.checked) {
      addCategory(selectedCategory);
    } else {
      removeCategory(id);
    }
  };

  // Save selected categories and navigate
  const handleSaveCategories = () => {
    if (user && isAddCategoryOpen) {
      setIsAddCategoryOpen(false);
      navigate("/room/create");
    } else {
      setError("Wystąpił problem spróbuj zalogować się ponownie.");
    }
  };

  return {
    selectedCategories,
    isAddCategoryOpen,
    handleCategoryCardCheck,
    handleSaveCategories,
  };
};
