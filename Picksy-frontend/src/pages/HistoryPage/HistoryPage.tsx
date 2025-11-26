import { Loading } from "../../components/Loading/Loading";
import { Error } from "../../components/Error/Error";
import { useHistoryPageLogic } from "../../hooks/useHistoryPageLogic";
import Navbar from "../../components/Navbar/Navbar";
import RoomList from "../../components/RoomList/RoomList";
import { useUser } from "../../context/userContext";
import "./HistoryPage.css";
import { useNavigate } from "react-router-dom";

const HistoryPage = () => {
  const { rooms, error, loading } = useHistoryPageLogic();
  const { user } = useUser();
  const navigate = useNavigate();

  const handleRoomClick = (roomCode: string) => {
    navigate(`/room/${roomCode}/results`);
  };

  if (error) {
    return <Error error={error} isRoomClosed={false} showResults={false} />;
  }

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="history-page">
      <Navbar />

      <div className="history-container">
        <div className="history-header">
          <h1>Historia pokoi</h1>
          <p className="subtitle">Twoje zakończone pokoje głosowania</p>
        </div>

        <RoomList
          rooms={rooms}
          currentUserId={user?.id || -1}
          onRoomClick={handleRoomClick}
        />
      </div>

      <style>{`
        
      `}</style>
    </div>
  );
};

export default HistoryPage;
