import React, { useState } from "react";
import "./CategoryHeader.css";
import { FaAngleDown, FaAngleUp } from "react-icons/fa";
import { VotingType } from "../../types/Voting";

type CategoryHeaderProp = {
  img: string;
  title: string;
  description: string;
  type: string;
};

export const CategoryHeader: React.FC<CategoryHeaderProp> = ({
  img,
  title,
  description,
  type,
}) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const desc =
    description && description !== "" ? description : "Brak opisu :-(";

  const handleDropdownClick = () => {
    setIsOpen((prev) => !prev);
  };

  return (
    <div className="category-header-wrapper">
      <div className="category-header-container">
        <div className="category-header-top">
          <div className="category-title-section">
            <h1 className="category-label">Aktualna kategoria</h1>
            <h2 className="category-header-title">{title}</h2>
          </div>
          <div className="voting-type-badge">
            {type === VotingType.PICK ? "Wielokrotny wybór" : "Swipe"}
          </div>
        </div>

        <div className={`category-header-content ${isOpen ? "open" : ""}`}>
          <div className="category-header-inner">
            <div className="category-header-image">
              <img src={img} alt={title} />
            </div>
            <div className="category-header-info">
              <div className="category-header-description">
                <p className="category-header-description-label">Opis</p>
                <p className="category-header-description-text">{desc}</p>
              </div>
            </div>
          </div>
        </div>

        <button
          className="dropdown-btn"
          onClick={handleDropdownClick}
          aria-expanded={isOpen}
          aria-label={isOpen ? "Zwiń szczegóły" : "Rozwiń szczegóły"}
        >
          <span className="dropdown-btn-text">
            {isOpen ? "Zwiń szczegóły" : "Pokaż szczegóły"}
          </span>
          <span className={`dropdown-icon ${isOpen ? "rotated" : ""}`}>
            {isOpen ? <FaAngleUp size={24} /> : <FaAngleDown size={24} />}
          </span>
        </button>
      </div>
    </div>
  );
};
