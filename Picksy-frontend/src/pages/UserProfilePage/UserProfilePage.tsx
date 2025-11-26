import { useState } from "react";
import { FaBan, FaCheck, FaTimes } from "react-icons/fa";
import { useNavigate, useParams } from "react-router-dom";
import { banUser, unbanUser } from "../../api/authApi";
import CategoryList from "../../components/CategoryList/CategoryList";
import Navbar from "../../components/Navbar/Navbar";
import { useUser } from "../../context/userContext";
import useUserProfileData from "../../hooks/useUserProfileData";
import "./UserProfilePage.css";

const UserProfilePage = () => {
  const userId: number = parseInt(useParams().userId || "-1");
  const navigate = useNavigate();
  const [showBanModal, setShowBanModal] = useState(false);
  const [isBanned, setIsBanned] = useState(false);
  const { user } = useUser();

  const {
    profile: userProfile,
    isLoadingProfile,
    userCategories,
    isLoadingCategories,
    setError,
    error,
  } = useUserProfileData(userId, setIsBanned);

  const handleCategoryClick = (id: number) => {
    navigate(`/category/${id}`);
  };

  const handleBanClick = () => {
    if (!isBanned) setShowBanModal(true);
    else handleUnban();
  };

  const handleUnban = async () => {
    const response = await unbanUser(userId);
    if (response.status !== 200)
      setError(
        "Wystapił nieoczekiwany błąd podczas odblokowywania użytkownika"
      );
    setIsBanned(false);
  };

  const handleBan = async (duration: number | null) => {
    const response = await banUser(userId, duration);
    if (response.status !== 200)
      setError("Wystapił nieoczekiwany błąd podczas blokowania użytkownika");
    else setIsBanned(true);
    setShowBanModal(false);
  };

  const handleCloseBanModal = () => {
    setShowBanModal(false);
  };
  if (isLoadingProfile) {
    return (
      <>
        <Navbar />
        <div className="user-profile-page">
          <div className="user-profile-header loading">
            <div className="avatar-skeleton"></div>
            <div className="user-profile-info-skeleton">
              <div className="skeleton-line username"></div>
              <div className="skeleton-line email"></div>
              <div className="skeleton-line bio"></div>
            </div>
          </div>
        </div>
      </>
    );
  }

  if (!userProfile || error) {
    return (
      <>
        <Navbar />
        <div className="user-profile-page">
          <div className="error-message">
            <h2>{error ? error : "Nie znaleziono profilu użytkownika"}</h2>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="user-profile-page">
        <div className="user-profile-header">
          <img
            src={userProfile.avatarUrl}
            alt={`${userProfile.username} avatar`}
            className="user-profile-avatar"
          />
          <div className="user-profile-info">
            {isBanned && <span className="banned-user">Zbanowany</span>}
            <div className="user-profile-info-top">
              <h1 className="user-profile-username">{userProfile.username}</h1>
              {user && user.role === "ADMIN" && user.id !== userProfile.id && (
                <button
                  className={`btn-delete ${isBanned && "unban-btn"}`}
                  onClick={handleBanClick}
                >
                  {isBanned ? <FaCheck size={16} /> : <FaBan size={16} />}

                  <span>{isBanned ? "Odblokuj" : "Zbanuj"}</span>
                </button>
              )}
            </div>

            <p className="user-profile-email">{userProfile.email}</p>
            <p className="user-profile-bio">{userProfile.bio}</p>
          </div>
        </div>

        <div className="user-profile-categories-section">
          <h2 className="section-title">Kategorie</h2>
          <CategoryList
            categoryListProps={userCategories}
            isLoading={isLoadingCategories}
            onCardClick={handleCategoryClick}
          />
        </div>
        {showBanModal && (
          <div className="ban-modal-overlay" onClick={handleCloseBanModal}>
            <div className="ban-modal" onClick={(e) => e.stopPropagation()}>
              <div className="ban-modal-header">
                <h2>Zbanuj użytkownika</h2>
                <button
                  className="close-modal-btn"
                  onClick={handleCloseBanModal}
                >
                  <FaTimes size={20} />
                </button>
              </div>
              <div className="ban-modal-content">
                <p>
                  Wybierz długość bana dla użytkownika{" "}
                  <strong>{userProfile.username}</strong>:
                </p>
                <div className="ban-options">
                  <button
                    className="ban-option-btn"
                    onClick={() => handleBan(3)}
                  >
                    3 dni
                  </button>
                  <button
                    className="ban-option-btn"
                    onClick={() => handleBan(7)}
                  >
                    7 dni
                  </button>
                  <button
                    className="ban-option-btn permanent"
                    onClick={() => handleBan(null)}
                  >
                    Na zawsze
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default UserProfilePage;
