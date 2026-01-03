import { useState, useEffect } from "react";
import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";
import type { SetInfo } from "../types/Set";

const LOCAL_STORAGE_KEY = "categories";

export const useCategories = () => {
  // Pobranie początkowej wartości z localStorage
  const [categories, setCategories] = useState<CategoryCardProps[]>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_KEY);
    return saved ? JSON.parse(saved) : [];
  });

  const [sets, setSets] = useState<Omit<SetInfo, "categories">[]>();

  // Synchronizacja z localStorage przy każdej zmianie
  useEffect(() => {
    if (categories.length > 0) {
      localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(categories));
    } else {
      localStorage.removeItem(LOCAL_STORAGE_KEY);
    }
  }, [categories]);

  const addCategory = (category: CategoryCardProps) => {
    setCategories((prev) => {
      if (prev.some((c) => c.id === category.id)) return prev;
      return [...prev, category];
    });
  };

  const removeCategory = (id: number) => {
    setCategories((prev) => prev.filter((c) => c.id !== id));
  };

  const clearCategories = () => setCategories([]);

  const removeSet = (id: number) => {
    setCategories(categories.filter((cat) => !(cat.set && cat.set.id === id)));
  };

  useEffect(() => {
    const sets: Omit<SetInfo, "categories">[] = [];
    categories.forEach((cat) => cat.set && sets.push(cat.set));
    setSets(sets);
  }, [categories]);

  return {
    categories,
    setCategories,
    addCategory,
    sets,
    removeSet,
    removeCategory,
    clearCategories,
  };
};
