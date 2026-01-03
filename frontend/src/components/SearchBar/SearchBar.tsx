import React from "react";
import { FaSearch } from "react-icons/fa";
import "./Searchbar.css";

export type SearchBarProps = {
  value: string;
  handleSearch: (query: React.ChangeEvent<HTMLInputElement>) => void;
};

const SearchBar: React.FC<SearchBarProps> = ({ value, handleSearch }) => {
  return (
    <div className="search-bar-container">
      <input
        placeholder="Wyszukaj"
        type="text"
        value={value}
        onChange={(e) => handleSearch(e)}
      />
      <FaSearch size="2rem" />
    </div>
  );
};

export default SearchBar;
