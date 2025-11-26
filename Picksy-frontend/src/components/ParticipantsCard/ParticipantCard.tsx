import { Users } from "lucide-react";
import React from "react";
import ParticipantList from "../ParticipantsList/ParicipantList";
import ParticipantsHeader from "../ParticipantsHeader/ParticipantsHeader";
import "./ParcitipantCard.css";
import type { Participant } from "../../types/Participant";

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
