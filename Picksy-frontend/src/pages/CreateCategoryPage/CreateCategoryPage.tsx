import { useRef, useState } from "react";
import { ImSpinner } from "react-icons/im";
import CategoryImageUpload from "../../components/CategoryImageUpload/CategoryImageUpload";
import CategoryOptions from "../../components/CategoryOptions/CategoryOptions";
import Navbar from "../../components/Navbar/Navbar";
import useCreateCategory from "../../hooks/useCreateCategory";
import type {
  CategoryDetails,
  CategoryType,
} from "../../types/CategoryDetails";
import "./CreateCategoryPage.css";

interface CreateCategoryPageProps {
  editMode?: boolean;
  categoryData?: CategoryDetails;
  onCancel?: () => void;
  onSave?: () => void;
}

const CreateCategoryPage: React.FC<CreateCategoryPageProps> = ({
  editMode = false,
  categoryData,
  onCancel,
  onSave,
}) => {
  const types: CategoryType[] = ["PICK", "SWIPE"];
  const [selectedType, setSelectedType] = useState<CategoryType>(
    categoryData?.type || "PICK"
  );
  const [isPublic, setIsPublic] = useState<boolean>(
    categoryData?.isPublic || false
  );
  const [title, setTitle] = useState<string>(categoryData?.name || "");
  const [description, setDescription] = useState<string>(
    categoryData?.description || ""
  );
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const {
    handleRemoveImage,
    handleFileChange,
    handleRemoveOption,
    handleOptionTitleChange,
    handleOptionImageChange,
    handleRemoveOptionImage,
    handleSubmit,
    handleAddOption,
    error,
    imagePreview,
    options,
  } = useCreateCategory(
    selectedType,
    isPublic,
    title,
    description,
    fileInputRef,
    editMode,
    categoryData?.id,
    onSave,
    categoryData?.options
  );

  const handleFormSubmit = async (e: React.FormEvent) => {
    setIsLoading(true);
    await handleSubmit(e);
    setIsLoading(false);
  };

  const handleCancelClick = () => {
    if (onCancel) {
      onCancel();
    }
  };

  if (isLoading) {
    return (
      <div className="profile-container">
        <div className="loading-spinner">
          <ImSpinner size={32} />
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="create-category-container">
        <div className="create-category-header">
          <h1>{editMode ? "Edytuj kategorię" : "Utwórz nową kategorię"}</h1>
        </div>

        <div className="create-category-form">
          <CategoryImageUpload
            fileInputRef={fileInputRef}
            imagePreview={imagePreview}
            onFileChange={handleFileChange}
            onRemoveImage={handleRemoveImage}
          />

          <div className="form-content">
            {error && <div className="error-message-create">{error}</div>}

            <form onSubmit={handleFormSubmit}>
              <div className="form-group">
                <label htmlFor="title">Nazwa kategorii</label>
                <input
                  type="text"
                  name="title"
                  id="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Np. Najlepsze pizzerie"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="type">Typ głosowania</label>
                <select
                  name="type"
                  id="type"
                  value={selectedType}
                  onChange={(e) =>
                    setSelectedType(() => {
                      if (e.target.value === "SWIPE") return "SWIPE";
                      return "PICK";
                    })
                  }
                >
                  {types.map((type, index) => (
                    <option key={index} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="description">Opis</label>
                <textarea
                  name="description"
                  id="description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Opisz swoją kategorię..."
                ></textarea>
              </div>

              <div className="form-group checkbox-group">
                <input
                  name="isPublic"
                  id="isPublic"
                  type="checkbox"
                  checked={isPublic}
                  onChange={() => setIsPublic((prev) => !prev)}
                />
                <label htmlFor="isPublic">
                  Kategoria publiczna (widoczna dla wszystkich)
                </label>
              </div>

              {/* Options Component */}
              <CategoryOptions
                options={options}
                onAddOption={handleAddOption}
                onRemoveOption={handleRemoveOption}
                onTitleChange={handleOptionTitleChange}
                onImageChange={handleOptionImageChange}
                onRemoveImage={handleRemoveOptionImage}
              />

              <div className="form-actions">
                {editMode && onCancel && (
                  <button
                    type="button"
                    className="cancel-btn"
                    onClick={handleCancelClick}
                  >
                    Anuluj
                  </button>
                )}
                <button type="submit" className="submit-btn">
                  {editMode ? "Zapisz zmiany" : "Utwórz kategorię"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateCategoryPage;
