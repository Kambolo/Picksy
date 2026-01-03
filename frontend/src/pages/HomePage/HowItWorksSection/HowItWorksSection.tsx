import { MdBookmarkAdd, MdHowToVote, MdLibraryAdd } from "react-icons/md";
import "./HowItWorksSection.css";
import { FaRegCopy } from "react-icons/fa";
import { FaSquarePollVertical } from "react-icons/fa6";

const HowItWorksSection = () => {
  const steps = [
    {
      number: "1",
      title: "Stwórz pokój",
      description:
        "Kliknij 'Głosowanie/Stwórz' i dodaj tytuł swojego głosowania",
      icon: <MdLibraryAdd />,
    },
    {
      number: "2",
      title: "Dodaj kategorie i opcje",
      description:
        "Stwórz zestawy z kategoriami i wypełnij je opcjami do wyboru",
      icon: <MdBookmarkAdd />,
    },
    {
      number: "3",
      title: "Udostępnij kod",
      description: "Przekaż kod pokoju uczestnikom",
      icon: <FaRegCopy />,
    },
    {
      number: "4",
      title: "Głosujcie razem",
      description: "Każdy może oddać swój głos w prostym interfejsie",
      icon: <MdHowToVote />,
    },
    {
      number: "5",
      title: "Zobacz wyniki",
      description: "Sprawdź wyniki głosowania",
      icon: <FaSquarePollVertical />,
    },
  ];

  return (
    <section id="how-it-works" className="home-section how-it-works-section">
      <h2 className="section-title">Jak to działa?</h2>
      <p className="section-subtitle">
        Pięć prostych kroków do zorganizowania głosowania
      </p>

      <div className="steps-container">
        {steps.map((step, index) => (
          <div key={index} className="step-card">
            <div className="step-icon">{step.icon}</div>
            <div className="step-number">{step.number}</div>
            <h3 className="step-title">{step.title}</h3>
            <p className="step-description">{step.description}</p>
          </div>
        ))}
      </div>
    </section>
  );
};

export default HowItWorksSection;
