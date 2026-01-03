import React from "react";
import type { ViewProps } from "../CategoryView/CategoryView";
import { useSetData } from "../../hooks/useSetData";
import SetList from "../SetList/SetList";
import { useNavigate } from "react-router-dom";
import { useSetSelection } from "../../hooks/useSetSelection";
import { useUser } from "../../context/userContext";

const SetView: React.FC<ViewProps> = ({
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
  const { user } = useUser();

  const { availableSets, error, setError, handleIncreaseViews } = useSetData(
    currentPage,
    sortByForApi,
    ascending,
    debouncedSearchValue,
    userId,
    setIsLoading,
    setTotalPages
  );

  const {
    selectedSets,
    selectedCategories,
    isAddSetOpen,
    handleSetCardCheck,
    handleSaveSets,
  } = useSetSelection(availableSets, setError);

  const handleCardClick = async (id: number) => {
    try {
      await handleIncreaseViews(id);

      navigate(`/set/${id}`);
    } catch (err) {
      console.error("Error handling card click:", err);
      setError("An error occurred while opening the category.");
    }
  };

  return (
    <>
      {isAddSetOpen && user && (
        <div className="category-save">
          <button onClick={handleSaveSets}>
            Zapisz wybrane kategorie ({selectedCategories.length})
          </button>
        </div>
      )}
      {error && <div className="error-message-category">{error}</div>}
      <SetList
        setListProps={availableSets}
        isLoading={isLoading}
        onCardClick={(id) => handleCardClick(id)}
        onChange={handleSetCardCheck}
        selectedSets={selectedSets}
      />
    </>
  );
};

export default SetView;
