import { useEffect, useState } from "react";
import type { Room } from "../types/Room";
import { getRoomsHistory } from "../api/roomApi";

type useHistoryPageLogicReturn = {
  rooms: Room[];
  error: string;
  loading: boolean;
};

export const useHistoryPageLogic = (): useHistoryPageLogicReturn => {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const loadRooms = async () => {
      setLoading(true);
      const response = await getRoomsHistory();
      if (response.status !== 200) {
        setError("Błąd podczas pobierania historii.");
        return;
      }

      const roomsFromApi: Room[] = response.result.map((room: any) => ({
        roomCode: room.roomCode,
        name: room.name,
        categoryIds: room.categoryIds,
        participants: room.participants,
        ownerId: room.ownerId,
        createdAt: room.createdAt,
      }));

      console.log(roomsFromApi);
      setRooms(roomsFromApi);
      setLoading(false);
    };

    loadRooms();
  }, []);

  return {
    rooms,
    error,
    loading,
  };
};
