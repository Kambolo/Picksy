import React from "react";
import type { Choice } from "../../types/Choice";
import ResultOption from "../ResultOption/ResultOption";
import { MdExpandLess, MdExpandMore } from "react-icons/md";

type ResultOptionListProps = {
  sortedOptions: Choice[];
  maxCount: number;
  getPercentage: (count: number) => number;
  handleShowMore: () => void;
  showMore: boolean;
};

const ResultOptionList: React.FC<ResultOptionListProps> = ({
  sortedOptions,
  maxCount,
  getPercentage,
  handleShowMore,
  showMore,
}) => {
  const visibleOptions = showMore ? sortedOptions : sortedOptions.slice(0, 4);

  return (
    <>
      <div
        className={`options-list ${
          sortedOptions.length > 4 && "expanding-list"
        }`}
      >
        {sortedOptions.length > 0 ? (
          visibleOptions.map((option, index) => {
            const percentage = getPercentage(option.count || 0);
            const isWinner = option.count === maxCount;

            return (
              <ResultOption
                key={option.id || index}
                option={option}
                percentage={percentage}
                isWinner={isWinner}
                index={index}
              />
            );
          })
        ) : (
          <p className="voting-has-not-started">Nie wybrano żadnej z opcji.</p>
        )}
      </div>
      {sortedOptions.length > 4 && !showMore && (
        <button onClick={handleShowMore} className="expand-button">
          Pokaż więcej <MdExpandMore />
        </button>
      )}
      {sortedOptions.length > 4 && showMore && (
        <button onClick={handleShowMore} className="expand-button">
          Pokaż mniej <MdExpandLess />
        </button>
      )}
    </>
  );
};

export default ResultOptionList;
