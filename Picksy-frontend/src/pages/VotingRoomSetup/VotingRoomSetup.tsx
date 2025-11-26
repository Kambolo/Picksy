import React, { useEffect } from "react";
import Navbar from "../../components/Navbar/Navbar";
import CategoryList from "../../components/CategoryList/CategoryList";
import { IoMdAdd } from "react-icons/io";
import { useUser } from "../../context/userContext";
import { useRoomSetup } from "../../hooks/useRoomSetup";
import "./VotingRoomSetup.css";
import { useNavigate } from "react-router-dom";
import { useCategoryUI } from "../../hooks/useCategoryUIContext";

const VotingRoomSetup: React.FC = () => {
  const { setIsAddCategoryOpen } = useCategoryUI();
  const { user } = useUser();

  const {
    roomName,
    setRoomName,
    categories,
    removeCategory,
    handleCreateRoom,
    error,
  } = useRoomSetup();

  useEffect(() => {
    setIsAddCategoryOpen(false);
  }, []);

  const navigate = useNavigate();

  if (!user) {
    return (
      <div>
        <Navbar />
        <div className="voting-room-container guest-user">
          Aby stworzyć pokój do głosowania musisz się zalogować.
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <Navbar />
        <div className="voting-room-container error-message-room">{error}</div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="voting-room-container">
        <div className="voting-room-content">
          <h1 className="title">Utwórz pokój głosowania</h1>

          <div className="card">
            <div className="form-group">
              <label className="label">Nazwa pokoju</label>
              <input
                type="text"
                value={roomName}
                onChange={(e) => setRoomName(e.target.value)}
                placeholder="np. Wieczór filmowy"
                className="input"
              />
            </div>

            <div className="categories-section">
              <div className="category-header-section">
                <label className="label">
                  Wybrane kategorie ({categories.length})
                </label>

                <button
                  className="add-category-btn"
                  onClick={() => {
                    setIsAddCategoryOpen(true);
                    navigate("/category");
                  }}
                >
                  <IoMdAdd size={24} color="white" />
                </button>
              </div>

              {categories.length === 0 && (
                <div className="empty-state">
                  Nie wybrano jeszcze żadnych kategorii
                </div>
              )}
            </div>
          </div>

          {categories.length > 0 && (
            <div className="selected-categories-wrapper">
              <CategoryList
                categoryListProps={categories}
                isLoading={false}
                onCardClick={removeCategory}
              />
              <p className="remove-hint">Kliknij kartę, aby usunąć z listy</p>
            </div>
          )}

          <button
            onClick={handleCreateRoom}
            disabled={!roomName.trim() || categories.length === 0}
            className="primary-button"
          >
            Utwórz pokój
          </button>
        </div>
      </div>
    </div>
  );
};

export default VotingRoomSetup;
