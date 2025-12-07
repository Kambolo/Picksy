import type { SetInfo } from "../../types/Set";
import type { Category } from "../../types/Voting";
import BlockerModal from "../BlockerModal/BlockerModal";
import { Voting } from "../Voting/Voting";

type RoomPageVotingProps = {
  category: Category | null;
  set: SetInfo | null;
  roomCode: string | undefined;
  isOwner: boolean;
  participantsCount: number;
  onNextCategory: () => Promise<void>;
  onEndVoting: () => Promise<void>;
  categoriesCount: number;
  currentCategoryIndex: number;
  isBlocked: boolean;
  confirmNavigation: (useOnLeave: boolean) => void;
  cancelNavigation: () => void;
};

export const RoomPageVoting: React.FC<RoomPageVotingProps> = ({
  category,
  set,
  roomCode,
  isOwner,
  participantsCount,
  onNextCategory,
  onEndVoting,
  categoriesCount,
  currentCategoryIndex,
  isBlocked,
  confirmNavigation,
  cancelNavigation,
}) => {
  return (
    <>
      <Voting
        category={category}
        set={set}
        roomCode={roomCode}
        isOwner={isOwner}
        participantsCount={participantsCount}
        onNextCategory={onNextCategory}
        onEndVoting={onEndVoting}
        categoriesCount={categoriesCount}
        currentCategory={currentCategoryIndex}
      />
      <BlockerModal
        isOpen={isBlocked}
        onAccept={confirmNavigation}
        onCancel={cancelNavigation}
      />
    </>
  );
};
