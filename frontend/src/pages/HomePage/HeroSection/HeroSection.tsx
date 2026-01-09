import { useNavigate } from "react-router-dom";
import "./HeroSection.css";
import screenImage from "../../../assets/Screen1.png";

const HeroSection = () => {
  const navigate = useNavigate();

  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    element?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <section className="hero-section">
      <div className="hero-content">
        <h1 className="hero-title">
          Organizuj głosowania{" "}
          <span className="hero-highlight">szybko i prosto</span>
        </h1>
        <p className="hero-description">
          Stwórz pokój, dodaj kategorie i opcje, a następnie pozwól wszystkim
          głosować. Dołączaj do głosowania bez rejestracji, bez komplikacji.
        </p>
        <div className="hero-buttons">
          <button
            className="btn-primary"
            onClick={() => navigate("/room/create")}
          >
            Stwórz pokój
          </button>
          <button
            className="btn-secondary"
            onClick={() => scrollToSection("how-it-works")}
          >
            Dowiedz się więcej
          </button>
        </div>
        <div className="hero-image">
          <img src={screenImage} alt="screen" />
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
