import { useNavigate } from "react-router-dom";
import { deleteCategory } from "../api/categoryApi";
import type { CategoryDetails } from "../types/CategoryDetails";
import { useState } from "react";

const useCategoryDetailsLogic = (
  category: CategoryDetails | null,
  fetchCategoryData: () => void,
  setLoading: React.Dispatch<React.SetStateAction<boolean>>
) => {
  const [isEditing, setIsEditing] = useState(false);
  const navigate = useNavigate();

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
  };

  const handleSaveEdit = async () => {
    await fetchCategoryData();
    setIsEditing(false);
  };

  const handleDeleteClick = async () => {
    setLoading(true);
    const response = category ? await deleteCategory(category.id) : null;
    console.log(response);

    if (response && response.status === 200) {
      setLoading(false);
      console.log("okej");
      navigate("/category");
    }
    setLoading(false);
  };

  return {
    isEditing,
    handleCancelEdit,
    handleEditClick,
    handleSaveEdit,
    handleDeleteClick,
  };
};

export default useCategoryDetailsLogic;
