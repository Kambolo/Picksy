import React from "react";

type CategoryImageUploadProp = {
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  onFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  imagePreview: string | null;
  onRemoveImage: () => void;
};

export const CategoryImageUpload: React.FC<CategoryImageUploadProp> = ({
  fileInputRef,
  onFileChange,
  imagePreview,
  onRemoveImage,
}) => {
  return (
    <div
      className={`category-image-upload-container ${
        imagePreview ? "has-image" : ""
      }`}
    >
      {imagePreview ? (
        <div className="category-image-preview">
          <img src={imagePreview} alt="Category preview" />
          <button
            type="button"
            className="remove-image-btn"
            onClick={onRemoveImage}
            aria-label="Usuń zdjęcie"
          >
            ×
          </button>
        </div>
      ) : (
        <div className="image-upload-placeholder">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
          <label htmlFor="category-image-upload">Dodaj zdjęcie kategorii</label>
          <p>Kliknij lub przeciągnij zdjęcie</p>
        </div>
      )}
      <input
        type="file"
        ref={fileInputRef}
        accept=".jpg,.jpeg,.png"
        onChange={onFileChange}
        id="category-image-upload"
      />
    </div>
  );
};

export default CategoryImageUpload;
