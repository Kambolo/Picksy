import React, { useState } from "react";
import {
  FaRegEdit,
  FaTrashAlt,
  FaLock,
  FaGlobe,
  FaPlus,
  FaCheck,
  FaSave,
} from "react-icons/fa";
import { Link, useNavigate, useParams } from "react-router-dom";
import CategoryList from "../../components/CategoryList/CategoryList";
import Navbar from "../../components/Navbar/Navbar";
import { useUser } from "../../context/userContext";
import { useSetDetailsData } from "../../hooks/useSetDetailsData";
import "./SetDetailsPage.css";
import { useSetDetailsPageLogic } from "../../hooks/useSetDetailsPageLogic";
import { formatViews } from "../../utils/formatViews";
import CreateCategoryPage from "../CreateCategoryPage/CreateCategoryPage";
import { useNavigationBlocker } from "../../hooks/useNavigationBloker";
import { deleteSet } from "../../api/categoryApi";
import BlockerModal from "../../components/BlockerModal/BlockerModal";

const SetDetailsPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useUser();
  const [isCreatingCategory, setIsCreatingCategory] = useState(false);

  const { set, error, setError, isLoading } = useSetDetailsData(
    parseInt(id || "0"),
    isCreatingCategory
  );

  const {
    handleDeleteClick,
    handleEditClick,
    handleCancelEdit,
    handleRemoveCategoryFromSet,
    handleCreateNewCategory,
    handleSaveChanges,
    handleTitleChange,
    handlePrivacyToggle,
    isRemoving,
    isEditMode,
    editedTitle,
    editedIsPublic,
    hasChanges,
  } = useSetDetailsPageLogic(
    parseInt(id || "0"),
    set,
    setError,
    setIsCreatingCategory
  );

  const { isBlocked, confirmNavigation, cancelNavigation } =
    useNavigationBlocker(
      () => (set && set?.categories.length > 1 ? false : true),
      [set?.categories],
      async () => {
        set && set.categories.length < 2 && (await deleteSet(set.id));
      }
    );

  if (isCreatingCategory) {
    return (
      <CreateCategoryPage
        onSave={() => setIsCreatingCategory(false)}
        setId={set ? set.id : undefined}
      />
    );
  }

  if (isLoading) {
    return (
      <>
        <Navbar />
        <div className="set-details-container">
          <div className="loading-spinner">
            <div className="spinner"></div>
          </div>
        </div>
      </>
    );
  }

  if (!set || error) {
    return (
      <>
        <Navbar />
        <div className="set-details-container">
          <div className="error-message">
            <h2>{error || "Set nie został odnaleziony."}</h2>
          </div>
        </div>
      </>
    );
  }

  const isOwner = set.authorId === user?.id || user?.role === "ADMIN";

  return (
    <>
      <Navbar />
      <div className="set-details-container">
        {isRemoving && (
          <div className="removing-overlay">
            <div className="spinner-small"></div>
            <span>Usuwanie kategorii...</span>
          </div>
        )}

        <div className="set-header">
          <div className="set-header-content">
            <div className="set-info">
              <div className="info-main">
                <div className="title-section">
                  {isEditMode ? (
                    <input
                      type="text"
                      className="set-title-input"
                      value={editedTitle}
                      onChange={(e) => handleTitleChange(e.target.value)}
                      placeholder="Nazwa zestawu..."
                    />
                  ) : (
                    <h1 className="set-title">{set.title}</h1>
                  )}

                  {isEditMode ? (
                    <button
                      className="visibility-badge-button"
                      onClick={handlePrivacyToggle}
                      title="Kliknij, aby zmienić widoczność"
                    >
                      {editedIsPublic ? (
                        <>
                          <FaGlobe size={14} />
                          <span>Publiczny</span>
                        </>
                      ) : (
                        <>
                          <FaLock size={14} />
                          <span>Prywatny</span>
                        </>
                      )}
                    </button>
                  ) : (
                    <div className="visibility-badge">
                      {set.isPublic ? (
                        <>
                          <FaGlobe size={14} />
                          <span>Publiczny</span>
                        </>
                      ) : (
                        <>
                          <FaLock size={14} />
                          <span>Prywatny</span>
                        </>
                      )}
                    </div>
                  )}
                </div>

                {isOwner && (
                  <div className="set-action-buttons">
                    {!isEditMode ? (
                      <>
                        <button
                          className="set-btn-edit"
                          onClick={handleEditClick}
                        >
                          <FaRegEdit size={18} />
                          <span>Edytuj</span>
                        </button>
                        <button
                          className="set-btn-delete"
                          onClick={handleDeleteClick}
                        >
                          <FaTrashAlt size={16} />
                          <span>Usuń</span>
                        </button>
                      </>
                    ) : (
                      <>
                        {hasChanges && (
                          <button
                            className="set-btn-save"
                            onClick={handleSaveChanges}
                            title="Zapisz zmiany"
                          >
                            <FaSave size={16} />
                            <span>Zapisz</span>
                          </button>
                        )}
                        <button
                          className="set-btn-done"
                          onClick={handleCancelEdit}
                        >
                          <FaCheck size={18} />
                          <span>Gotowe</span>
                        </button>
                      </>
                    )}
                  </div>
                )}
              </div>

              <div className="set-meta">
                {set.authorId ? (
                  <Link to={`/profile/${set.authorId}`} className="meta-link">
                    <span className="meta-item author-name">
                      {set.author || "Unknown"}
                    </span>
                  </Link>
                ) : (
                  <span className="meta-item">{set.author || "Unknown"}</span>
                )}
                <span className="meta-divider">•</span>
                <span className="meta-item">
                  {set.categories.length}{" "}
                  {set.categories.length === 1 ? "kategoria" : "kategorie"}
                </span>
                <span className="meta-divider">•</span>
                <span className="meta-item">
                  {formatViews(set.views)} wyświetleń
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="categories-section">
          <div className="categories-header">
            <h2 className="categories-title">
              Kategorie ({set.categories?.length})
            </h2>

            {isEditMode && isOwner && (
              <div className="set-edit-mode-buttons">
                <button
                  className="set-btn-add-new"
                  onClick={handleCreateNewCategory}
                  title="Utwórz nową kategorię"
                >
                  <FaPlus size={16} />
                  <span>Nowa kategoria</span>
                </button>
              </div>
            )}
          </div>

          <CategoryList
            categoryListProps={set.categories || []}
            isLoading={false}
            onCardClick={(categoryId) => {
              if (!isEditMode) {
                navigate(`/category/${categoryId}`);
              }
            }}
            showRemove={isEditMode}
            handleRemove={handleRemoveCategoryFromSet}
            canSelect={false}
          />
        </div>
      </div>

      <BlockerModal
        isOpen={isBlocked}
        onAccept={confirmNavigation}
        onCancel={cancelNavigation}
        text={`Twój zestaw ma obecnie mniej niż 2 kategorie.\nJeśli teraz opuścisz to miejsce, twój zestaw nie zostanie stworzony.`}
      />
    </>
  );
};

export default SetDetailsPage;
