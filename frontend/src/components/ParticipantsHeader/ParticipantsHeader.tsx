import { Users } from "lucide-react";
import React from "react";
import "./ParticipantsHeader.css";

type headerProp = {
  participantsCount: number;
};

export const ParticipantsHeader: React.FC<headerProp> = ({
  participantsCount,
}) => {
  return (
    <div className="participants-header">
      <Users size={24} />
      <h2 className="participants-title">Uczestnicy ({participantsCount})</h2>
    </div>
  );
};

export default ParticipantsHeader;
