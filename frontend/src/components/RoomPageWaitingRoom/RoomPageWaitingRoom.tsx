import Navbar from "../Navbar/Navbar";
import BlockerModal from "../BlockerModal/BlockerModal";
import RoomHeader from "../RoomHeader/RoomHeader";
import ParticipantCard from "../ParticipantsCard/ParticipantCard";
import OwnerActions from "../OwnerActions/OwnerActions";
import type { Participant } from "../../types/Participant";
import { Error } from "../Error/Error";

type RoomPageWaitingRoomProps = {
  roomCode: string;
  copied: boolean;
  onCopy: () => void;
  onLeave: () => void;
  participants: Participant[];
  participant: Participant | null;
  ownerId: number;
  isConnected: boolean;
  onStartVoting: () => Promise<void>;
  roomError: string | null;
  isBlocked: boolean;
  confirmNavigation: (useOnLeave: boolean) => void;
  cancelNavigation: () => void;
};

export const RoomPageWaitingRoom: React.FC<RoomPageWaitingRoomProps> = ({
  roomCode,
  copied,
  onCopy,
  onLeave,
  roomError,
  participants,
  participant,
  ownerId,
  isConnected,
  onStartVoting,
  isBlocked,
  confirmNavigation,
  cancelNavigation,
}) => {
  if (roomError)
    return <Error error={roomError} isRoomClosed={false} showResults={false} />;
  return (
    <div>
      <Navbar />
      <div className="room-page-container">
        <div className="room-page-content">
          <RoomHeader
            roomCode={roomCode}
            copied={copied}
            isConnected={isConnected}
            onCopy={onCopy}
            onLeave={onLeave}
          />

          <ParticipantCard
            participants={participants}
            currentParticipant={participant}
            ownerId={ownerId}
            isConnected={isConnected}
          />

          {participant?.id === ownerId && (
            <OwnerActions
              onStartVoting={onStartVoting}
              disabled={!isConnected || participants.length < 2}
            />
          )}
        </div>
      </div>

      <BlockerModal
        isOpen={isBlocked}
        onAccept={confirmNavigation}
        onCancel={cancelNavigation}
      />
    </div>
  );
};
