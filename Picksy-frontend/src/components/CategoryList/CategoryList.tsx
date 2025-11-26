import React from "react";
import CategoryCard, {
  type CategoryCardProps,
} from "../CategoryCard/CategoryCard";
import "./CategoryList.css";

export type CategoryListProps = {
  categoryListProps: CategoryCardProps[];
  isLoading?: boolean;
  onCardClick?: (id: number) => void;
  onChange?: (id: number, e: React.ChangeEvent<HTMLInputElement>) => void;
  selectedCategories?: CategoryCardProps[];
};

const CategoryList: React.FC<CategoryListProps> = ({
  categoryListProps,
  isLoading = false,
  onCardClick,
  onChange,
  selectedCategories,
}) => {
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

  return (
    <div className="category-list-container">
      <div className="category-list-wrapper">
        {categoryListProps.map((prop: CategoryCardProps, index: number) => (
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
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryList;
