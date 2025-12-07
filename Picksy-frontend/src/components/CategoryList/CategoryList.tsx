import React, { type JSX, useState } from "react";
import CategoryCard, {
  type CategoryCardProps,
} from "../CategoryCard/CategoryCard";
import "./CategoryList.css";

export type CategoryListProps = {
  categoryListProps: CategoryCardProps[];
  isLoading?: boolean;
  onCardClick?: (id: number) => void;
  onSetClick?: (id: number) => void;
  onChange?: (id: number, e: React.ChangeEvent<HTMLInputElement>) => void;
  selectedCategories?: CategoryCardProps[];
  showRemove?: boolean;
  handleRemove?: (id: number) => void;
  canSelect?: boolean;
  showSet?: boolean;
};

const CategoryList: React.FC<CategoryListProps> = ({
  categoryListProps,
  isLoading = false,
  onCardClick,
  onSetClick,
  onChange,
  selectedCategories,
  showRemove,
  handleRemove,
  canSelect = true,
  showSet = false,
}) => {
  const [expandedSets, setExpandedSets] = useState<Set<number>>(new Set());

  const toggleSet = (setId: number) => {
    const newExpanded = new Set(expandedSets);
    if (newExpanded.has(setId)) {
      newExpanded.delete(setId);
    } else {
      newExpanded.add(setId);
    }
    setExpandedSets(newExpanded);
  };

  if (isLoading) {
    return (
      <div className="category-list-container">
        <div className="category-list-wrapper">
          {[...Array(3)].map((_, index) => (
            <div key={index} className="category-list-item">
              <CategoryCard
                id={0}
                img=""
                title=""
                author=""
                authorId={-1}
                description=""
                type=""
                isLoading={true}
                isPublic={false}
                views={0}
              />
            </div>
          ))}
        </div>
      </div>
    );
  }

  const renderCategories = () => {
    if (!showSet) {
      return categoryListProps.map((prop: CategoryCardProps, index: number) => (
        <div key={index} className="category-list-item">
          <CategoryCard
            id={prop.id}
            img={prop.img}
            title={prop.title}
            author={prop.author}
            authorId={prop.authorId}
            description={prop.description}
            type={prop.type}
            onClick={() => onCardClick?.(prop.id)}
            onChange={onChange}
            selectedCategories={selectedCategories}
            isPublic={prop.isPublic}
            showIsPublic={prop.showIsPublic}
            views={prop.views}
            handleRemove={handleRemove}
            showRemove={showRemove}
            canSelect={canSelect}
          />
        </div>
      ));
    }
    const grouped = new Map<number | null, CategoryCardProps[]>();

    categoryListProps.forEach((prop) => {
      const setId = prop.set?.id ?? null;
      if (!grouped.has(setId)) {
        grouped.set(setId, []);
      }
      grouped.get(setId)!.push(prop);
    });

    const result: JSX.Element[] = [];

    grouped.forEach((categories, setId) => {
      if (setId !== null && categories.length > 0 && categories[0].set) {
        const setInfo = categories[0].set;
        const isExpanded = expandedSets.has(setId);

        result.push(
          <div
            key={`set-${setId}`}
            className="category-set-container"
            onClick={() => onSetClick && onSetClick(setId)}
          >
            <div className="category-set-header">
              <div className="category-set-header-content">
                <h3 className="category-set-title">{setInfo.title}</h3>
                <button
                  className="category-set-toggle"
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleSet(setId);
                  }}
                  aria-label={isExpanded ? "Zwiń set" : "Rozwiń set"}
                >
                  <svg
                    className={`category-set-toggle-icon ${
                      isExpanded ? "expanded" : ""
                    }`}
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                  >
                    <polyline points="6 9 12 15 18 9" />
                  </svg>
                </button>
              </div>
              <div className="category-set-info">
                <span className="category-set-author">
                  Autor: {setInfo.author}
                </span>
                <span className="category-set-count">
                  Kategorie: {categories.length}
                </span>
                <span className="category-set-views">
                  Wyświetlenia: {setInfo.views}
                </span>
                {setInfo.isPublic !== undefined && (
                  <span
                    className={`category-set-visibility ${
                      setInfo.isPublic ? "public" : "private"
                    }`}
                  >
                    {setInfo.isPublic ? "Publiczny" : "Prywatny"}
                  </span>
                )}
              </div>
            </div>
            {isExpanded && (
              <div className="category-set-items">
                {categories.map((prop: CategoryCardProps, index: number) => (
                  <div key={`${setId}-${index}`} className="category-list-item">
                    <CategoryCard
                      id={prop.id}
                      img={prop.img}
                      title={prop.title}
                      author={prop.author}
                      authorId={prop.authorId}
                      description={prop.description}
                      type={prop.type}
                      onChange={onChange}
                      selectedCategories={selectedCategories}
                      isPublic={prop.isPublic}
                      showIsPublic={prop.showIsPublic}
                      views={prop.views}
                      handleRemove={handleRemove}
                      showRemove={showRemove}
                      canSelect={canSelect}
                    />
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      } else {
        categories.forEach((prop: CategoryCardProps, index: number) => {
          result.push(
            <div
              key={`no-set-${prop.id}-${index}`}
              className="category-list-item"
            >
              <CategoryCard
                id={prop.id}
                img={prop.img}
                title={prop.title}
                author={prop.author}
                authorId={prop.authorId}
                description={prop.description}
                type={prop.type}
                onClick={() => onCardClick?.(prop.id)}
                onChange={onChange}
                selectedCategories={selectedCategories}
                isPublic={prop.isPublic}
                showIsPublic={prop.showIsPublic}
                views={prop.views}
                handleRemove={handleRemove}
                showRemove={showRemove}
                canSelect={canSelect}
              />
            </div>
          );
        });
      }
    });

    return result;
  };

  return (
    <div className="category-list-container">
      <div className="category-list-wrapper">{renderCategories()}</div>
    </div>
  );
};

export default CategoryList;
