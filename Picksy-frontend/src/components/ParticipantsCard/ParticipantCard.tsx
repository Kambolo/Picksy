import React from "react";
import type { Participant } from "../../types/Participant";
import ParticipantsHeader from "../ParticipantsHeader/ParticipantsHeader";
import ParticipantList from "../ParticipantsList/ParicipantList";
import "./ParcitipantCard.css";

type ParticipantCardProp = {
  participants: Participant[];
  ownerId: number;
  currentParticipant: Participant | null;
  isConnected: boolean;
};

const ParticipantCard: React.FC<ParticipantCardProp> = ({
  participants,
  ownerId,
  currentParticipant,
  isConnected,
}) => {
  return (
    <div className="participants-card">
      <ParticipantsHeader participantsCount={participants.length} />
      <ParticipantList
        participants={participants}
        ownerId={ownerId}
        currentParticipant={currentParticipant}
        isConnected={isConnected}
      />
    </div>
  );
};

export default ParticipantCard;
