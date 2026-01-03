import React from "react";

interface OwnerActionsProps {
  onStartVoting: () => void;
  disabled?: boolean;
}

const OwnerActions: React.FC<OwnerActionsProps> = ({
  onStartVoting,
  disabled,
}) => (
  <div className="room-actions">
    <button
      className="primary-button start-voting-button"
      disabled={disabled}
      onClick={onStartVoting}
    >
      Rozpocznij głosowanie
    </button>
  </div>
);

export default OwnerActions;
