import { MdOutlineAlternateEmail } from "react-icons/md";
import "./ContactSection.css";
import { FaBug, FaRegLightbulb } from "react-icons/fa";

const ContactSection = () => {
  return (
    <section id="contact" className="home-section contact-section">
      <h2 className="section-title">Skontaktuj się z nami</h2>
      <p className="section-subtitle">
        Masz pytania lub sugestie? Chętnie Cię wysłuchamy!
      </p>

      <div className="contact-content">
        <div className="contact-card">
          <div className="contact-icon">
            <MdOutlineAlternateEmail />
          </div>
          <h3 className="contact-title">Email</h3>
          <a href="mailto:picksy@gmail.com" className="contact-link">
            picksy@gmail.com
          </a>
          <p className="contact-description">
            Odpowiadamy zwykle w ciągu 24 godzin
          </p>
        </div>

        <div className="contact-card">
          <div className="contact-icon">
            <FaRegLightbulb />
          </div>
          <h3 className="contact-title">Sugestie</h3>
          <p className="contact-description">
            Twoja opinia jest dla nas ważna. Napisz do nas maila, jeśli masz
            pomysł na nową funkcję!
          </p>
        </div>

        <div className="contact-card">
          <div className="contact-icon">
            <FaBug />
          </div>
          <h3 className="contact-title">Zgłoś problem</h3>
          <p className="contact-description">
            Znalazłeś błąd? Daj nam znać, a postaramy się go jak najszybciej
            naprawić.
          </p>
        </div>
      </div>
    </section>
  );
};

export default ContactSection;
