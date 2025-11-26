import { createContext, useContext } from "react";

type CategoryUIContextType = {
  isAddCategoryOpen: boolean;
  setIsAddCategoryOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export const CategoryUIContext = createContext<
  CategoryUIContextType | undefined
>(undefined);

export const useCategoryUI = () => {
  const ctx = useContext(CategoryUIContext);
  if (!ctx) {
    throw new Error("useCategoryUI must be used within CategoryUIProvider");
  }
  return ctx;
};
