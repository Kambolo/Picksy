import "./BlockerModal.css";

type Props = {
  isOpen: boolean;
  onAccept: (onLeave: boolean) => void;
  onCancel: () => void;
};

const BlockerModal = ({ isOpen, onAccept, onCancel }: Props) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <p className="modal-text">Czy chcesz opuścić pokój?</p>
        <div className="modal-buttons">
          <button className="btn-leave" onClick={() => onAccept(true)}>
            Wyjdź
          </button>
          <button className="btn-stay" onClick={onCancel}>
            Zostań
          </button>
        </div>
      </div>
    </div>
  );
};

export default BlockerModal;
