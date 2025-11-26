import React, { useState, useRef, useEffect } from "react";
import { Link } from "react-router-dom";
import { ChevronDown } from "lucide-react";
import "./DropdownMenu.css";

export interface DropdownItem {
  title: string;
  to?: string;
  onClick?: () => void;
}

interface DropdownMenuProps {
  title: string;
  items: DropdownItem[];
}

const DropdownMenu: React.FC<DropdownMenuProps> = ({ title, items }) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const isSingleItem = items.length === 1;

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleToggle = () => {
    setIsOpen(!isOpen);
  };

  const handleItemClick = () => {
    setIsOpen(false);
  };

  // If there's only one item, render as a direct link or button
  if (isSingleItem) {
    const item = items[0];

    if (item.onClick) {
      return (
        <div className="dropdown-menu single-item">
          <button
            onClick={item.onClick}
            className="dropdown-toggle-link dropdown-button"
          >
            {title}
          </button>
        </div>
      );
    }

    if (item.to) {
      return (
        <div className="dropdown-menu single-item">
          <Link to={item.to} className="dropdown-toggle-link">
            {title}
          </Link>
        </div>
      );
    }
  }

  // Otherwise render as dropdown
  return (
    <div className="dropdown-menu" ref={dropdownRef}>
      <button
        className={`dropdown-toggle ${isOpen ? "active" : ""}`}
        onClick={handleToggle}
      >
        {title}
        <ChevronDown
          size={16}
          className={`dropdown-icon ${isOpen ? "rotated" : ""}`}
        />
      </button>
      {isOpen && (
        <div className="dropdown-content">
          {items.map((item, index) => {
            if (item.onClick) {
              return (
                <button
                  key={index}
                  onClick={() => {
                    item.onClick?.();
                    handleItemClick();
                  }}
                  className="dropdown-item dropdown-item-button"
                >
                  {item.title}
                </button>
              );
            }

            if (item.to) {
              return (
                <Link
                  key={index}
                  to={item.to}
                  className="dropdown-item"
                  onClick={handleItemClick}
                >
                  {item.title}
                </Link>
              );
            }

            return null;
          })}
        </div>
      )}
    </div>
  );
};

export default DropdownMenu;
