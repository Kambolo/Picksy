import React, { useEffect, useState } from "react";
import type { Option } from "../../types/Option";
import "./PickVoting.css";
import { loadState, saveState } from "../../utils/persistState";

interface PickVotingProps {
  options: Option[];
  onVote: (selectedOptions: number[]) => void;
  hasVoted: boolean;
  roomCode: string;
  categoryId: number;
}

export const PickVoting: React.FC<PickVotingProps> = ({
  options,
  onVote,
  hasVoted,
  roomCode,
  categoryId,
}) => {
  const [selected, setSelected] = useState<Set<number>>(new Set());
  const [isLoaded, setIsLoaded] = useState(false);

  // load state
  useEffect(() => {
    const saved = loadState(`pickVoting-${roomCode}-${categoryId}`);
    if (saved) setSelected(new Set(saved.selected));
    setIsLoaded(true);
  }, []);

  //save state
  useEffect(() => {
    if (!isLoaded) return;
    saveState(`pickVoting-${roomCode}-${categoryId}`, {
      selected: Array.from(selected),
    });
  }, [selected]);

  const toggleOption = (optionId: number) => {
    if (hasVoted) return;

    const newSelected = new Set(selected);
    if (newSelected.has(optionId)) {
      newSelected.delete(optionId);
    } else {
      newSelected.add(optionId);
    }
    setSelected(newSelected);
  };

  const handleSubmit = () => {
    if (selected.size > 0) {
      onVote(Array.from(selected));
    }
  };

  return (
    <div className="pick-voting">
      <div className="pick-options-grid">
        {options.map((option, index) => (
          <div
            key={option.id}
            className={`pick-option-card ${
              selected.has(option.id) ? "selected" : ""
            } ${hasVoted ? "disabled" : ""}`}
            onClick={() => toggleOption(option.id)}
          >
            <div className="pick-option-letter">
              {String.fromCharCode(65 + index)}
            </div>
            <div className="pick-option-image-container">
              {option.photoURL ? (
                <img
                  src={option.photoURL}
                  alt={option.name}
                  className="pick-option-image"
                />
              ) : (
                <div className="pick-option-placeholder">No Image</div>
              )}
            </div>
            <div className="pick-option-name">
              <div>{option.name}</div>
            </div>
            {selected.has(option.id) && <div className="checkmark">✓</div>}
          </div>
        ))}
      </div>

      {!hasVoted && (
        <button
          className="submit-vote-btn"
          onClick={handleSubmit}
          disabled={selected.size === 0}
        >
          Wyślij głos ({selected.size} wybranych)
        </button>
      )}

      {hasVoted && <div className="voted-message">✓ Głos został przesłany</div>}
    </div>
  );
};
