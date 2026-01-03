import { useState } from "react";
import { useParams } from "react-router-dom";
import CategoryView from "../../components/CategoryView/CategoryView";
import Navbar from "../../components/Navbar/Navbar";
import PageNavigation from "../../components/PageNavigation/PageNavigation";
import SetView from "../../components/SetView/SetView";
import ViewToggle from "../../components/ViewToggle/ViewToggle";
import { usePaginationAndSort } from "../../hooks/usePaginationAndSort";
import "./ViewPage.css";

export const ViewPage = () => {
  const { userId } = useParams<{ userId: string }>();

  const [isLoading, setIsLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(1);

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
    activeView,
    handleActiveViewChange,
  } = usePaginationAndSort();

  return (
    <div className="page-container">
      <Navbar />
      <ViewToggle
        activeView={activeView}
        onViewChange={handleActiveViewChange}
      />
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
        {activeView === "kategorie" ? (
          <CategoryView
            currentPage={currentPage}
            sortByForApi={sortByForApi}
            ascending={ascending}
            debouncedSearchValue={debouncedSearchValue}
            userId={parseInt(userId ?? "0")}
            isLoading={isLoading}
            setIsLoading={setIsLoading}
            setTotalPages={setTotalPages}
          />
        ) : (
          <SetView
            currentPage={currentPage}
            sortByForApi={sortByForApi}
            ascending={ascending}
            debouncedSearchValue={debouncedSearchValue}
            userId={parseInt(userId ?? "0")}
            isLoading={isLoading}
            setIsLoading={setIsLoading}
            setTotalPages={setTotalPages}
          />
        )}
      </div>
    </div>
  );
};
