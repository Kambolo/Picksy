import React from "react";
import "./CategoryCard.css";

import { FaLock, FaLockOpen } from "react-icons/fa6";
import { Link } from "react-router-dom";
import { useCategoryUI } from "../../hooks/useCategoryUIContext";
import type { SetInfo } from "../../types/Set";

export type CategoryCardProps = {
  id: number;
  img: string;
  title: string;
  author: string;
  authorId: number;
  description: string;
  type: string;
  isPublic: boolean;
  showIsPublic?: boolean;
  views: number;
  set?: Omit<SetInfo, "categories">;
};

type CategoryCardComponentProps = CategoryCardProps & {
  isLoading?: boolean;
  onClick?: () => void;
  onChange?: (id: number, e: React.ChangeEvent<HTMLInputElement>) => void;
  selectedCategories?: CategoryCardProps[];
  showRemove?: boolean;
  handleRemove?: (id: number) => void;
  canSelect?: boolean;
  isPartOfSet?: boolean;
};

const CategoryCard: React.FC<CategoryCardComponentProps> = ({
  id,
  img,
  title,
  author,
  authorId,
  description,
  type,
  isPublic,
  showIsPublic = false,
  isLoading = false,
  views,
  onClick,
  onChange,
  selectedCategories,
  showRemove,
  handleRemove,
  canSelect,
  isPartOfSet = false,
}) => {
  const { isAddCategoryOpen } = useCategoryUI();

  if (isLoading) {
    return (
      <div className="category-card-container skeleton">
        <div className="category-image-container">
          <div className="skeleton-image" />
        </div>
        <div className="category-card-center">
          <div className="skeleton-title" />
          <div className="skeleton-author" />
          <div className="category-description">
            <div className="skeleton-label" />
            <div className="skeleton-text" />
            <div className="skeleton-text" />
            <div className="skeleton-text short" />
          </div>
        </div>
        <div className="category-card-type">
          <div className="skeleton-type-label" />
          <div className="skeleton-type-value" />
        </div>
      </div>
    );
  }

  const isChecked = selectedCategories?.some((cat) => cat.id === id) ?? false;

  return (
    <div className="category-card-container" onClick={onClick}>
      {isAddCategoryOpen && canSelect && (
        <div
          className="add-category-checkbox"
          onClick={(e) => e.stopPropagation()}
        >
          <input
            type="checkbox"
            id={`check-${id}`}
            onChange={(e) => {
              if (onChange) onChange(id, e);
            }}
            checked={isChecked}
          />
          <label htmlFor={`check-${id}`} />
        </div>
      )}
      <div className="category-image-container">
        <img src={img} alt={title} />
      </div>
      <div className="category-card-center">
        <h2 className="category-card-title">{title}</h2>
        <h3 className="category-card-views">Wyświetlenia: {views}</h3>
        {author != "" && authorId > 0 && isPartOfSet ? (
          <Link
            to={`/profile/${authorId}`}
            onClick={(e) => e.stopPropagation()}
            className="card-link"
          >
            <h3 className="category-card-author">Autor: {author}</h3>
          </Link>
        ) : (
          authorId === -1 && (
            <h3 className="category-card-author">Autor: Picksy</h3>
          )
        )}

        <div className="category-description">
          <p className="category-description-label">Opis</p>
          <p className="category-description-text">
            {!description && "Brak opisu"}
            {description.length < 120 && description}
            {description.length >= 120 && description.substring(0, 120) + "..."}
          </p>
        </div>
      </div>

      <div className="category-card-privacy">
        {showIsPublic && isPublic && <FaLockOpen size={24} color="#888" />}
        {showIsPublic && !isPublic && <FaLock size={24} color="#888" />}
      </div>

      <div className="category-card-type">
        <p className="category-type-label">Typ</p>
        <p className="category-type-value">{type}</p>
      </div>
      {showRemove && (
        <button
          className="btn-delete card-delete"
          onClick={() => handleRemove && handleRemove(id)}
        >
          Usuń
        </button>
      )}
    </div>
  );
};

export default CategoryCard;
