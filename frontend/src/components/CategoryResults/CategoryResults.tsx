import React from "react";
import type { Choice, ResultCategory } from "../../types/Choice";
import "./CategoryResults.css";
import ResultOptionList from "../ResultOptionList/ResultOptionList";
import ResultCategoryHeader from "../ResultCategoryHeader/ResultCategoryHeader";

interface CategoryResultProps {
  category: ResultCategory;
  handleShowMore: () => void;
  showMore: boolean;
}

export const CategoryResults: React.FC<CategoryResultProps> = ({
  category,
  handleShowMore,
  showMore,
}) => {
  let sortedOptions: Choice[] = category.options.sort(
    (a, b) => b.count - a.count
  );
  // If options have count == 0 dont show them in extended list
  sortedOptions = sortedOptions.filter((choice) => choice.count > 0);

  const maxCount: number | undefined = sortedOptions.at(0)?.count; //get the max votes

  const getPercentage = (count: number) => {
    return category.participantsCount > 0
      ? (count / category.participantsCount) * 100
      : 0;
  };

  return (
    <div className="result-container">
      <ResultCategoryHeader
        id={category.id}
        name={category.name}
        participants={category.participantsCount}
      />

      {/* Results */}
      <div className="results-section ">
        <h2 className="results-title">Wyniki głosowania</h2>

        <ResultOptionList
          sortedOptions={sortedOptions}
          maxCount={maxCount || 0}
          getPercentage={getPercentage}
          handleShowMore={handleShowMore}
          showMore={showMore}
        />
      </div>
    </div>
  );
};
