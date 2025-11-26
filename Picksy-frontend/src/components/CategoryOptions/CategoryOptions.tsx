import React from "react";
import "./CategoryOptions.css";

export interface CategoryOption {
  id: number;
  title: string;
  image: File | null;
  imagePreview: string | null;
}

interface CategoryOptionsProps {
  options: CategoryOption[];
  onAddOption: () => void;
  onRemoveOption: (optionId: number) => void;
  onTitleChange: (optionId: number, newTitle: string) => void;
  onImageChange: (
    optionId: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => void;
  onRemoveImage: (optionId: number) => void;
}

const CategoryOptions: React.FC<CategoryOptionsProps> = ({
  options,
  onAddOption,
  onRemoveOption,
  onTitleChange,
  onImageChange,
  onRemoveImage,
}) => {
  return (
    <div className="options-section">
      <div className="options-header">
        <label>Opcje odpowiedzi</label>
        <button type="button" className="add-option-btn" onClick={onAddOption}>
          + Dodaj opcję
        </button>
      </div>

      <div className="options-list">
        {options.map((option, index) => (
          <div key={option.id} className="option-item">
            <div className="option-header">
              <span className="option-number">Opcja {index + 1}</span>
              <button
                type="button"
                className="remove-option-btn"
                onClick={() => onRemoveOption(option.id)}
              >
                ×
              </button>
            </div>

            <div className="option-content">
              <div className="option-image-upload">
                {option.imagePreview ? (
                  <div className="option-image-preview">
                    <img src={option.imagePreview} alt={`Opcja ${index + 1}`} />
                    <button
                      type="button"
                      className="remove-image-btn"
                      onClick={() => onRemoveImage(option.id)}
                    >
                      ×
                    </button>
                  </div>
                ) : (
                  <label className="option-image-placeholder">
                    <input
                      type="file"
                      accept="image/*"
                      onChange={(e) => onImageChange(option.id, e)}
                    />
                    <div className="placeholder-content">
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
                      <span>Dodaj zdjęcie</span>
                    </div>
                  </label>
                )}
              </div>

              <input
                type="text"
                className="option-title-input"
                placeholder="Nazwa opcji"
                value={option.title}
                onChange={(e) => onTitleChange(option.id, e.target.value)}
                required
              />
            </div>
          </div>
        ))}
      </div>

      {options.length === 0 && (
        <p className="no-options-message">
          Dodaj opcje, z których użytkownicy będą mogli wybierać
        </p>
      )}
    </div>
  );
};

export default CategoryOptions;
