import { useNavigate } from "react-router-dom";
import { closeRoom } from "../../api/roomApi";
import { Loading } from "../../components/Loading/Loading";
import { Error } from "../../components/Error/Error";
import { RoomPageVoting } from "../../components/RoomPageVoting/RoomPageVoting";
import { RoomPageWaitingRoom } from "../../components/RoomPageWaitingRoom/RoomPageWaitingRoom";
import { useRoomPageLogic } from "../../hooks/useRoomPageLogic";
import "./RoomPage.css";

const RoomPage = () => {
  const {
    roomCode,
    wsError,
    isRoomClosed,
    categoryId,
    isLoadingCategory,
    currentCategory,
    showResults,
    isBlocked,
    confirmNavigation,
    cancelNavigation,
    votingProps,
    waitingRoomProps,
  } = useRoomPageLogic();

  const navigate = useNavigate();

  // error or closed
  if (!roomCode || wsError || (isRoomClosed && !showResults))
    return (
      <Error
        error={wsError}
        isRoomClosed={isRoomClosed}
        showResults={showResults}
      />
    );

  // loading category
  if (categoryId !== -1 && (isLoadingCategory || !currentCategory))
    return <Loading />;

  // voting finished
  if (showResults) {
    if (isBlocked) confirmNavigation(false);
    if (votingProps.isOwner) closeRoom(roomCode);
    navigate(`room/${roomCode}/results`);
  }

  // user is voting
  if (currentCategory)
    return (
      <RoomPageVoting
        {...votingProps}
        isBlocked={isBlocked}
        confirmNavigation={confirmNavigation}
        cancelNavigation={cancelNavigation}
      />
    );

  // user is waiting in room
  return (
    <RoomPageWaitingRoom
      {...waitingRoomProps}
      isBlocked={isBlocked}
      confirmNavigation={confirmNavigation}
      cancelNavigation={cancelNavigation}
    />
  );
};

export default RoomPage;
