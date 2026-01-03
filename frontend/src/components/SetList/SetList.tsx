import React from "react";
import SetCard, { type SetCardProps } from "../SetCard/SetCard";
import "./SetList.css";
import type { SetInfo } from "../../types/Set";

type SetListProps = {
  setListProps: SetCardProps[];
  isLoading?: boolean;
  onCardClick?: (id: number) => void;
  onChange?: (id: number, e: React.ChangeEvent<HTMLInputElement>) => void;
  selectedSets?: Omit<SetInfo, "categories">[];
};

const SetList: React.FC<SetListProps> = ({
  setListProps,
  isLoading = false,
  onCardClick,
  onChange,
  selectedSets,
}) => {
  if (isLoading) {
    return (
      <div className="set-list-container">
        {[...Array(5)].map((_, index) => (
          <SetCard
            key={`skeleton-${index}`}
            id={0}
            title=""
            author=""
            authorId={0}
            categoryCount={0}
            isPublic={false}
            views={0}
            isLoading={true}
            showIsPublic={false}
            categories={[]}
          />
        ))}
      </div>
    );
  }

  if (setListProps.length === 0) {
    return (
      <div className="set-list-container">
        <div className="set-list-empty">
          <p>Nie znaleziono żadnych zestawów</p>
        </div>
      </div>
    );
  }

  return (
    <div className="set-list-container">
      {setListProps.map((set) => (
        <SetCard
          key={set.id}
          {...set}
          onClick={() => onCardClick?.(set.id)}
          onChange={onChange}
          selectedSets={selectedSets}
        />
      ))}
    </div>
  );
};

export default SetList;
