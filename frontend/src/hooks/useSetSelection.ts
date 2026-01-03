import { useNavigate } from "react-router-dom";
import type { SetCardProps } from "../components/SetCard/SetCard";
import { useUser } from "../context/userContext";
import { useCategoryUI } from "./useCategoryUIContext";
import { useRoomSetup } from "./useRoomSetup";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import { useState } from "react";

export const useSetSelection = (
  availableSets: SetCardProps[],
  setError: (error: string) => void
) => {
  const navigate = useNavigate();
  const { user } = useUser();
  const {
    isAddCategoryOpen: isAddSetOpen,
    setIsAddCategoryOpen: setIsAddSetOpen,
  } = useCategoryUI();
  const {
    addCategory,
    removeCategory,
    categories: selectedCategories,
    sets: selectedSets,
  } = useRoomSetup();

  // Handle checkbox change
  const handleSetCardCheck = (
    id: number,
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const selectedSet = availableSets.find((set) => set.id === id);

    if (!selectedSet) return;

    // Get all categories for set
    const categories: CategoryCardProps[] = selectedSet.categories.map(
      (cat: CategoryCardProps) => ({
        ...cat,
        set: { ...selectedSet },
      })
    );

    // If set is selected add, save all its categories to localStorage
    if (e.target.checked) {
      categories.forEach((cat) => addCategory(cat));
    } else {
      categories.forEach((cat) => removeCategory(cat.id));
    }
  };

  // Save selected categories and navigate
  const handleSaveSets = () => {
    if (user && isAddSetOpen) {
      setIsAddSetOpen(false);
      navigate("/room/create");
    } else {
      setError("Wystąpił problem spróbuj zalogować się ponownie.");
    }
  };

  return {
    selectedCategories,
    selectedSets,
    isAddSetOpen,
    handleSetCardCheck,
    handleSaveSets,
  };
};
