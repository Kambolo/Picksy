import { Crown, User, Users } from "lucide-react";
import type { Room } from "../../types/Room";

type RoomCardProps = {
  room: Room;
  currentUserId: number;
  onClick: () => void;
};

const RoomCard: React.FC<RoomCardProps> = ({
  room,
  currentUserId,
  onClick,
}) => {
  const isOwner = room.ownerId === currentUserId;
  const participantCount = room.participants
    ? Object.keys(room.participants).length
    : 0;

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    if (diffDays === 0) {
      return `Dzisiaj o ${hours}:${minutes}`;
    } else if (diffDays === 1) {
      return `Wczoraj o ${hours}:${minutes}`;
    } else if (diffDays < 7) {
      return `${diffDays} dni temu`;
    } else {
      return date.toLocaleDateString("pl-PL", {
        hour: "2-digit",
        minute: "2-digit",
        day: "numeric",
        month: "long",
        year: "numeric",
      });
    }
  };

  return (
    <div className="room-card" onClick={onClick}>
      <div className="room-card-header">
        <h3 className="room-name">{room.name}</h3>
        <span className="room-code">#{room.roomCode}</span>
      </div>

      <div className="room-date">
        <span>{formatDate(room.createdAt)}</span>
      </div>

      <div className="room-info-section">
        <div className="info-item">
          <Users size={18} />
          <span>
            {participantCount}{" "}
            {participantCount === 1 ? "uczestnik" : "uczestników"}
          </span>
        </div>

        <div className={`role-badge ${isOwner ? "owner" : "participant"}`}>
          {isOwner ? (
            <>
              <Crown size={16} />
              <span>Właściciel</span>
            </>
          ) : (
            <>
              <User size={16} />
              <span>Uczestnik</span>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default RoomCard;
