import { LogIn, Users } from "lucide-react";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { getRoomDetails } from "../../api/roomApi";
import Navbar from "../../components/Navbar/Navbar";
import { useUser } from "../../context/userContext";
import "./JoinRoomPage.css";

const JoinRoom: React.FC = () => {
  const [roomCode, setRoomCode] = useState<string>("");
  const [username, setUsername] = useState<string>("");
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();
  const { user } = useUser();

  const handleJoinRoom = async () => {
    setError("");

    // Validation
    if (!roomCode.trim()) {
      setError("Kod pokoju jest wymagany");
      return;
    }

    if (!user && !username.trim()) {
      setError("Nazwa użytkownika jest wymagana");
      return;
    }

    setIsLoading(true);

    const response = await getRoomDetails(roomCode);
    if (response.status !== 200) {
      setError("Wpisz poprawny kod pokoju");
      setIsLoading(false);
      return;
    }
    if (response.result.room_closed) setError("Pokój zamknięty");
    else if (response.result.voting_started)
      setError("Głosowanie juz się rozpoczęło");
    else {
      const participants = Object.entries(response.result.participants);
      const id = user ? user.id : -participants.length;

      navigate("/room/" + roomCode, { state: { id, username } });
    }
    setIsLoading(false);
  };

  const handleRoomCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // Convert to uppercase and limit to 6 characters
    const value = e.target.value.slice(0, 7);
    setRoomCode(value);
  };

  return (
    <div>
      <Navbar />
      <div className="join-room-container">
        <div className="join-room-content">
          <div className="join-room-header">
            <LogIn size={48} className="join-room-icon" />
            <h1 className="join-room-title">Dołącz do pokoju</h1>
            <p className="join-room-subtitle">
              Wprowadź kod pokoju, aby dołączyć do głosowania
            </p>
          </div>

          <div className="join-room-card">
            <div className="form-group">
              <label className="label">
                Kod pokoju <span className="required">*</span>
              </label>
              <input
                type="text"
                value={roomCode}
                onChange={handleRoomCodeChange}
                placeholder="np. ABC123"
                className="input code-input"
                maxLength={7}
                disabled={isLoading}
              />
              <p className="input-hint">Kod składa się z 7 znaków</p>
            </div>

            {!user && (
              <div className="form-group">
                <label className="label">
                  Twoja nazwa <span className="required">*</span>
                </label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="np. Jan Kowalski"
                  className="input"
                  disabled={isLoading}
                />
                <p className="input-hint">Jako gość musisz podać swoją nazwę</p>
              </div>
            )}

            {user && (
              <div className="user-info-box">
                <Users size={20} />
                <div className="user-info-text">
                  <p className="user-info-label">Dołączasz jako:</p>
                  <p className="user-info-name">{user.username}</p>
                </div>
              </div>
            )}

            {error && (
              <div className="error-message">
                <span className="error-icon">⚠️</span>
                {error}
              </div>
            )}

            {isLoading && (
              <div className="loading-message">
                <span className="loading-spinner">⏳</span>
                Łączenie z pokojem...
              </div>
            )}

            <button
              onClick={handleJoinRoom}
              disabled={
                isLoading || !roomCode.trim() || (!user && !username.trim())
              }
              className="primary-button join-button"
            >
              {isLoading ? "Dołączanie..." : "Dołącz do pokoju"}
            </button>
          </div>

          <div className="join-room-footer">
            <p className="footer-text-room">
              Nie masz kodu pokoju?{" "}
              <button
                onClick={() => navigate("/room/create")}
                className="footer-link-room"
              >
                Stwórz własny pokój
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default JoinRoom;
