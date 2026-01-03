import React from "react";
import SearchBar, { type SearchBarProps } from "../SearchBar/SearchBar";
import "./PageNavigation.css";

export type SortOption = "views" | "newest" | "oldest" | "alphabetical";

export type PageNavigationProps = SearchBarProps & {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  sortBy?: SortOption;
  onSortChange: (sort: SortOption) => void;
};

const PageNavigation: React.FC<PageNavigationProps> = ({
  value,
  handleSearch,
  currentPage,
  totalPages,
  onPageChange,
  sortBy,
  onSortChange,
}) => {
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible + 2) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);

      if (currentPage > 3) {
        pages.push("...");
      }

      const start = Math.max(2, currentPage - 1);
      const end = Math.min(totalPages - 1, currentPage + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (currentPage < totalPages - 2) {
        pages.push("...");
      }

      pages.push(totalPages);
    }

    return pages;
  };

  return (
    <div className="page-navigation-container">
      <div className="page-navigation-content">
        <div className="page-navigation-top">
          <SearchBar value={value} handleSearch={handleSearch} />

          {sortBy && (
            <div className="filter-dropdown">
              <label htmlFor="sort-select" className="filter-label">
                Sortuj według:
              </label>
              <select
                id="sort-select"
                className="filter-select"
                value={sortBy}
                onChange={(e) => onSortChange(e.target.value as SortOption)}
              >
                <option value="views">Wyświetlenia</option>
                <option value="newest">Najnowsze</option>
                <option value="oldest">Najstarsze</option>
                <option value="alphabetical">Alfabetycznie</option>
              </select>
            </div>
          )}
        </div>

        {totalPages > 1 && (
          <div className="pagination-container">
            <button
              className="pagination-arrow"
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 1}
              aria-label="Previous page"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M12.5 15L7.5 10L12.5 5"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            </button>

            <div className="pagination-numbers">
              {getPageNumbers().map((page, index) => (
                <React.Fragment key={index}>
                  {page === "..." ? (
                    <span className="pagination-ellipsis">...</span>
                  ) : (
                    <button
                      className={`pagination-number ${
                        currentPage === page ? "active" : ""
                      }`}
                      onClick={() => onPageChange(page as number)}
                    >
                      {page}
                    </button>
                  )}
                </React.Fragment>
              ))}
            </div>

            <button
              className="pagination-arrow"
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
              aria-label="Next page"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M7.5 15L12.5 10L7.5 5"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default PageNavigation;
