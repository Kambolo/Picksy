import { useNavigate } from "react-router-dom";
import "./Footer.css";

const Footer = () => {
  const navigate = useNavigate();

  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    element?.scrollIntoView({ behavior: "smooth" });
  };

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-section">
          <h3 className="footer-logo">Picksy</h3>
          <p className="footer-description">
            Prosta aplikacja do organizowania głosowań online. Bez komplikacji.
          </p>
        </div>

        <div className="footer-section">
          <h4 className="footer-title">Nawigacja</h4>
          <ul className="footer-links">
            <li>
              <button onClick={scrollToTop} className="footer-link">
                Strona główna
              </button>
            </li>
            <li>
              <button
                onClick={() => scrollToSection("how-it-works")}
                className="footer-link"
              >
                Jak to działa
              </button>
            </li>
            <li>
              <button
                onClick={() => navigate("/room/create")}
                className="footer-link"
              >
                Stwórz pokój
              </button>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h4 className="footer-title">Kontakt</h4>
          <ul className="footer-contact">
            <li>
              <a href="mailto:picksy@gmail.com" className="footer-link">
                picksy@gmail.com
              </a>
            </li>
            <li className="footer-text">Odpowiadamy w ciągu 24h</li>
          </ul>
        </div>

        <div className="footer-section">
          <h4 className="footer-title">O aplikacji</h4>
          <p className="footer-text">
            Picksy to darmowe narzędzie stworzone z myślą o prostych i szybkich
            głosowaniach.
          </p>
        </div>
      </div>

      <div className="footer-bottom">
        <p className="footer-copyright">
          © 2025 Picksy. Wszelkie prawa zastrzeżone.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
