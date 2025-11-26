import React from "react";
import type { Participant } from "../../types/Participant";
import { FaCrown } from "react-icons/fa6";
import "./ParticipantList.css";

type ParticipantListProp = {
  participants: Participant[];
  ownerId: number;
  currentParticipant: Participant | null;
  isConnected: boolean;
};

export const ParticipantList: React.FC<ParticipantListProp> = ({
  participants,
  ownerId,
  currentParticipant,
  isConnected,
}) => {
  return (
    <div className="participants-list">
      {participants.length === 0 ? (
        <div className="empty-participants">
          {isConnected
            ? "Oczekiwanie na uczestników..."
            : "Łączenie z pokojem..."}
        </div>
      ) : (
        participants.map((item) => (
          <div key={item.id} className="participant-item">
            <div>
              <img
                className="participant-avatar"
                src={item.photoUrl}
                alt={item.username}
              />
            </div>
            <span className="participant-name">
              {item.username}
              {currentParticipant && currentParticipant.id === item.id && (
                <span className="you-badge">(Ty)</span>
              )}
              {item.id === ownerId && <FaCrown size={20} />}
            </span>
          </div>
        ))
      )}
    </div>
  );
};

export default ParticipantList;
