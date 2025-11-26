import React from "react";
import type { Choice } from "../../types/Choice";

type ResultOptionProps = {
  option: Choice;
  isWinner: boolean;
  percentage: number;
  index: number;
};

const ResultOption: React.FC<ResultOptionProps> = ({
  option,
  isWinner,
  percentage,
  index,
}) => {
  return (
    <div key={option.id} className="option-wrapper">
      {/* Winner badge */}
      {isWinner && (
        <div className="winner-badge">
          <svg className="winner-icon" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
          </svg>
          Zwycięzca
        </div>
      )}

      <div className={`option-card ${isWinner ? "winner" : ""}`}>
        <div className="option-content-wrapper">
          {/* Progress bar */}
          <div
            className={`progress-bar color-${index % 5}`}
            style={{ width: `${percentage}%` }}
          />

          {/* Content */}
          <div className="option-content">
            <div className="option-left">
              <span className="option-position">#{index + 1}</span>
              <span className="option-name">{option.name}</span>
            </div>

            <div className="option-right">
              <div className="votes-info">
                <div className="votes-count">{option.count || 0}</div>
                <div className="votes-label">głosów</div>
              </div>
              <div className="percentage">{percentage.toFixed(1)}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResultOption;
