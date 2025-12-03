import type { Room } from "../../types/Room";
import RoomCard from "../RoomCard/RoomCard";

export type RoomListProps = {
  rooms: Room[];
  currentUserId: number;
  onRoomClick: (roomCode: string) => void;
};

const RoomList: React.FC<RoomListProps> = ({
  rooms,
  currentUserId,
  onRoomClick,
}) => {
  if (rooms.length === 0) {
    return (
      <div className="empty-state">
        <p>Nie masz jeszcze żadnej historii pokoi.</p>
      </div>
    );
  }

  return (
    <div className="room-list">
      {rooms.map((room) => (
        <RoomCard
          key={room.roomCode}
          room={room}
          currentUserId={currentUserId}
          onClick={() => onRoomClick(room.roomCode)}
        />
      ))}
    </div>
  );
};

export default RoomList;
