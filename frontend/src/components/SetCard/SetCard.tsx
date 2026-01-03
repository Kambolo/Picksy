import React from "react";
import "./SetCard.css";
import { FaLock, FaLockOpen } from "react-icons/fa6";
import { Link } from "react-router-dom";
import type { SetInfo } from "../../types/Set";
import { useCategoryUI } from "../../hooks/useCategoryUIContext";

export type SetCardProps = SetInfo & {
  categoryCount: number;
  isLoading?: boolean;
  onClick?: () => void;
  selectedSets?: Omit<SetInfo, "categories">[];
  onChange?: (id: number, e: React.ChangeEvent<HTMLInputElement>) => void;
};

const SetCard: React.FC<SetCardProps> = ({
  id,
  title,
  author,
  authorId,
  categoryCount,
  isPublic,
  showIsPublic = false,
  isLoading = false,
  views,
  onClick,
  selectedSets,
  onChange,
}) => {
  const { isAddCategoryOpen } = useCategoryUI();

  if (isLoading) {
    return (
      <div className="set-card-container skeleton">
        <div className="set-card-content">
          <div className="skeleton-title" />
          <div className="skeleton-author" />
          <div className="skeleton-text" />
        </div>
        <div className="set-card-type">
          <div className="skeleton-type-label" />
          <div className="skeleton-type-value" />
        </div>
      </div>
    );
  }

  const isChecked = selectedSets?.some((set) => set.id === id) ?? false;

  return (
    <div className="set-card-container" onClick={onClick}>
      {isAddCategoryOpen && (
        <div
          className="add-category-checkbox"
          onClick={(e) => e.stopPropagation()}
        >
          <input
            type="checkbox"
            id={`check-${id}`}
            onChange={(e) => {
              console.log(onChange);
              if (onChange) onChange(id, e);
            }}
            checked={isChecked}
          />
          <label htmlFor={`check-${id}`} />
        </div>
      )}
      <div className="set-card-content">
        <div className="set-card-top">
          <h2 className="set-card-title">{title}</h2>
          <h3 className="set-card-views">Wyświetlenia: {views}</h3>
        </div>

        {author !== "" && authorId ? (
          <Link
            to={`/profile/${authorId}`}
            onClick={(e) => e.stopPropagation()}
            className="card-link"
          >
            <h3 className="set-card-author">Autor: {author}</h3>
          </Link>
        ) : (
          <h3 className="set-card-author">Autor: {author}</h3>
        )}

        <div className="set-card-info">
          <p className="set-card-category-count">
            Liczba kategorii: <span>{categoryCount}</span>
          </p>
        </div>
      </div>

      <div className="set-card-privacy">
        {showIsPublic && isPublic && <FaLockOpen size={24} color="#888" />}
        {showIsPublic && !isPublic && <FaLock size={24} color="#888" />}
      </div>

      <div className="set-card-type">
        <p className="set-type-label">Typ</p>
        <p className="set-type-value">Zestaw</p>
      </div>
    </div>
  );
};

export default SetCard;
