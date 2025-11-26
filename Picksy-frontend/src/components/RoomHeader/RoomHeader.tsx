import React from "react";
import { LogOut, Copy, Check } from "lucide-react";

interface RoomHeaderProps {
  roomCode: string;
  copied: boolean;
  isConnected: boolean;
  onCopy: () => void;
  onLeave: () => void;
}

const RoomHeader: React.FC<RoomHeaderProps> = ({
  roomCode,
  copied,
  isConnected,
  onCopy,
  onLeave,
}) => {
  return (
    <div className="room-header-card">
      <div className="room-header-info">
        <h1 className="room-title">Pokój głosowania</h1>
        <div className="room-code-section">
          <span className="room-code-label">Kod:</span>
          <span className="room-code-display">{roomCode}</span>
          <button
            onClick={onCopy}
            className={`copy-icon-button ${copied ? "copied" : ""}`}
            title="Kopiuj kod"
          >
            {copied ? <Check size={20} /> : <Copy size={20} />}
          </button>
        </div>
        <div className="connection-status">
          <div
            className={`status-indicator ${
              isConnected ? "connected" : "disconnected"
            }`}
          />
          <span className="status-text">
            {isConnected ? "Połączono" : "Rozłączono"}
          </span>
        </div>
      </div>
      <button onClick={onLeave} className="leave-room-button">
        <LogOut size={20} /> Opuść pokój
      </button>
    </div>
  );
};

export default RoomHeader;
