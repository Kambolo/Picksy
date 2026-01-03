import { useEffect, useState } from "react";
import { CategoryUIContext } from "../hooks/useCategoryUIContext";

export const CategoryUIProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  // Initialize from localStorage if exists
  const [isAddCategoryOpen, setIsAddCategoryOpen] = useState<boolean>(() => {
    const saved = localStorage.getItem("isAddCategoryOpen");
    return saved ? JSON.parse(saved) : false;
  });

  // Save to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem(
      "isAddCategoryOpen",
      JSON.stringify(isAddCategoryOpen)
    );
  }, [isAddCategoryOpen]);

  return (
    <CategoryUIContext.Provider
      value={{ isAddCategoryOpen, setIsAddCategoryOpen }}
    >
      {children}
    </CategoryUIContext.Provider>
  );
};
