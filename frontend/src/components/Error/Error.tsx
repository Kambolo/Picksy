import Navbar from "../Navbar/Navbar";

export const Error = ({
  error,
  isRoomClosed,
  showResults,
}: {
  error: string | null;
  isRoomClosed: boolean;
  showResults: boolean;
}) => {
  const message =
    error ||
    (isRoomClosed && !showResults
      ? `Pokój został zamknięty`
      : `Nie znaleziono pokoju`);

  return (
    <div>
      <Navbar />
      <div className="room-page-container">
        <div className="error-state">{message}</div>
      </div>
    </div>
  );
};
