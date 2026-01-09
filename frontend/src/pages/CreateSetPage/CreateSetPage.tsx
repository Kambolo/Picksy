import { useState } from "react";
import { ImSpinner } from "react-icons/im";
import { createSet } from "../../api/categoryApi";
import Navbar from "../../components/Navbar/Navbar";
import { useNavigate } from "react-router-dom";

const CreateSetPage = () => {
  const [isPublic, setIsPublic] = useState<boolean>(false);
  const [title, setTitle] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleFormSubmit = async () => {
    setIsLoading(true);
    const response = await createSet(title, isPublic);
    if (response.error) {
      setError("Wystapił błąd: " + error);
    } else {
      navigate(`/set/${response.result.id}`);
    }
    setIsLoading(false);
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
          <h1>{"Utwórz nowy zestaw"}</h1>
        </div>

        <div className="create-category-form">
          <div className="form-content">
            {error && <div className="error-message-create">{error}</div>}

            <form onSubmit={handleFormSubmit}>
              <div className="form-group">
                <label htmlFor="title">Nazwa zestawu</label>
                <input
                  type="text"
                  name="title"
                  id="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Np. Wakacje 2026"
                  required
                />
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
                  Zestaw publiczny (widoczny dla wszystkich)
                </label>
              </div>

              <button type="submit" className="submit-btn">
                Utwórz Zestaw
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateSetPage;
