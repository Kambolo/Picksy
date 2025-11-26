import React from "react";
import { FaRegEdit, FaTrashAlt } from "react-icons/fa";
import { Link, useParams } from "react-router-dom";
import Navbar from "../../components/Navbar/Navbar";
import { useUser } from "../../context/userContext";
import useCategoryDetailsData from "../../hooks/useCategoryDetailsData";
import useCategoryDetailsLogic from "../../hooks/useCategoryDetailsLogic";
import CreateCategoryPage from "../CreateCategoryPage/CreateCategoryPage";
import "./CategoryDetailsPage.css";

const CategoryDetailsPage: React.FC = () => {
  const { id } = useParams();
  const { user } = useUser();

  const { category, loading, setLoading, fetchCategoryData } =
    useCategoryDetailsData(parseInt(id || "-1"));
  const {
    isEditing,
    formatViews,
    handleCancelEdit,
    handleEditClick,
    handleSaveEdit,
    handleDeleteClick,
  } = useCategoryDetailsLogic(category, fetchCategoryData, setLoading);

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="category-details-container">
          <div className="loading-spinner">
            <div className="spinner"></div>
          </div>
        </div>
      </>
    );
  }

  if (!category) {
    return (
      <>
        <Navbar />
        <div className="category-details-container">
          <div className="error-message-details">
            <h2>Kategoria nie odnaleziona.</h2>
          </div>
        </div>
      </>
    );
  }

  // Show edit form if editing
  if (isEditing && (category.authorID === user?.id || user?.role === "ADMIN")) {
    return (
      <CreateCategoryPage
        editMode={true}
        categoryData={category}
        onCancel={handleCancelEdit}
        onSave={handleSaveEdit}
      />
    );
  }

  return (
    <>
      <Navbar />
      <div className="category-details-container">
        <div className="category-header">
          <div className="category-banner-wrapper">
            <div
              className="banner-background-blur"
              style={{ backgroundImage: `url(${category.photoURL})` }}
            ></div>
            <img
              src={category.photoURL}
              alt={`${category.name} banner`}
              className="category-banner-image"
            />
            <div className="banner-overlay"></div>
          </div>

          <div className="category-info">
            <div className="info-main">
              <div>
                <h1 className="category-details-title">{category.name}</h1>
                <p className="category-details-description">
                  {category.description}
                </p>
              </div>
              {(category.authorID === user?.id || user?.role === "ADMIN") && (
                <div className="action-buttons">
                  <button className="btn-edit" onClick={handleEditClick}>
                    <FaRegEdit size={18} />
                    <span>Edytuj</span>
                  </button>
                  <button className="btn-delete" onClick={handleDeleteClick}>
                    <FaTrashAlt size={16} />
                    <span>Usuń</span>
                  </button>
                </div>
              )}
            </div>

            <div className="category-meta">
              {category.authorID ? (
                <Link
                  to={`/profile/${category.authorID}`}
                  onClick={(e) => e.stopPropagation()}
                  className="card-link"
                >
                  <span className="meta-item">
                    {category.author || "Unknown"}
                  </span>
                </Link>
              ) : (
                <span className="meta-item">
                  {category.author || "Unknown"}
                </span>
              )}

              <span className="meta-divider">•</span>
              <span className="meta-item">
                {formatViews(category.views)} wyświetlenia
              </span>
              <span className="meta-divider">•</span>
              <span className="meta-item">
                {new Date(category.created).toLocaleDateString("en-GB", {
                  year: "numeric",
                  month: "2-digit",
                  day: "2-digit",
                })}
              </span>
            </div>
          </div>
        </div>

        <div className="options-section">
          <h2 className="options-title">{category.options.length} Opcje</h2>

          <div className="options-grid">
            {category.options.map((option) => (
              <div key={option.id} className="option-card">
                <div className="option-image-wrapper">
                  <img
                    src={option.photoURL}
                    alt={option.name}
                    className="option-image"
                  />
                </div>
                <h3 className="option-name">{option.name}</h3>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
};

export default CategoryDetailsPage;
