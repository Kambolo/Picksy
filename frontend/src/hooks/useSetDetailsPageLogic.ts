import { useEffect, useState } from "react";
import {
  deleteSet,
  removeCategoryFromSet,
  updateSet,
} from "../api/categoryApi";
import type { SetDetails } from "./useSetDetailsData";
import { useNavigate } from "react-router-dom";

export const useSetDetailsPageLogic = (
  setId: number,
  set: SetDetails | null,
  setError: (msg: string) => void,
  setCreateCategory: (isCreating: boolean) => void
) => {
  const [isRemoving, setIsRemoving] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editedTitle, setEditedTitle] = useState("");
  const [editedIsPublic, setEditedIsPublic] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (set) {
      setEditedTitle(set.title);
      setEditedIsPublic(set.isPublic);
    }
  }, [set]);

  useEffect(() => {
    if (set) {
      const titleChanged = editedTitle !== set.title;
      const privacyChanged = editedIsPublic !== set.isPublic;
      setHasChanges(titleChanged || privacyChanged);
    }
  }, [editedTitle, editedIsPublic, set]);

  const handleEditClick = () => {
    setIsEditMode(true);
  };

  const handleCancelEdit = async () => {
    if (hasChanges) {
      await handleSaveChanges();
    }
    setCreateCategory(false);
    setIsEditMode(false);
  };

  const handleSaveChanges = async () => {
    if (!set || !hasChanges) return;
    const response = await updateSet(editedTitle, editedIsPublic, setId);

    if (response.error) {
      setError("Wystąpił problem: " + response.error);
      return;
    }

    set.title = response.result.name;
    set.isPublic = response.result.isPublic;
    setHasChanges(false);

    console.log("Saving changes:", {
      setId,
      title: editedTitle,
      isPublic: editedIsPublic,
    });
  };

  const handleTitleChange = (newTitle: string) => {
    setEditedTitle(newTitle);
  };

  const handlePrivacyToggle = () => {
    setEditedIsPublic(!editedIsPublic);
  };

  const handleDeleteClick = async () => {
    if (!window.confirm("Czy na pewno chcesz usunąć ten zestaw?")) {
      return;
    }
    setIsRemoving(true);
    const response = await deleteSet(setId);
    if (response.error) {
      setError("Wystąpił problem: " + response.error);
      return;
    }
    navigate(-1);
  };

  const handleRemoveCategoryFromSet = async (categoryId: number) => {
    if (!window.confirm("Czy na pewno chcesz usunąć tę kategorię z zestawu?")) {
      return;
    }
    setIsRemoving(true);
    const response = await removeCategoryFromSet(setId, categoryId);
    if (response.error) {
      setError("Wystapił problem: " + response.error);
    }
    if (set && set.categories)
      set.categories = set.categories.filter((cat) => cat.id !== categoryId);
    setIsRemoving(false);
  };

  const handleCreateNewCategory = () => {
    setCreateCategory(true);
  };

  return {
    handleEditClick,
    handleCancelEdit,
    handleDeleteClick,
    handleRemoveCategoryFromSet,
    handleCreateNewCategory,
    handleSaveChanges,
    handleTitleChange,
    handlePrivacyToggle,
    isRemoving,
    isEditMode,
    setCreateCategory,
    editedTitle,
    editedIsPublic,
    hasChanges,
  };
};
