import "./ViewToggle.css";

interface ViewToggleProps {
  activeView: "kategorie" | "zestawy";
  onViewChange: (view: "kategorie" | "zestawy") => void;
}

const ViewToggle = ({ activeView, onViewChange }: ViewToggleProps) => {
  return (
    <div className="view-toggle-container">
      <button
        onClick={() => onViewChange("kategorie")}
        className={`view-toggle-button ${
          activeView === "kategorie" ? "active" : ""
        }`}
      >
        Kategorie
      </button>
      <button
        onClick={() => onViewChange("zestawy")}
        className={`view-toggle-button ${
          activeView === "zestawy" ? "active" : ""
        }`}
      >
        Zestawy
      </button>
    </div>
  );
};

export default ViewToggle;
