import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getRoomDetails } from "../api/roomApi";
import type { Participant } from "../types/Participant";
import fetchPhotoUrl from "./useUserPhotoUrlProvider";

interface UseRoomDetailsReturn {
  participants: Participant[];
  ownerId: number;
  error: string | null;
  setError: React.Dispatch<React.SetStateAction<string | null>>;
  isLoading: boolean;
}

export function useRoomDetails(roomCode?: string): UseRoomDetailsReturn {
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [ownerId, setOwnerId] = useState<number>(-1);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (!roomCode) {
      setError("Brak kodu pokoju");
      setIsLoading(false);
      return;
    }

    const fetchRoomData = async () => {
      try {
        setIsLoading(true);
        const response = await getRoomDetails(roomCode);

        if (response.status !== 200) {
          setError("Pokój nie istnieje");
          return;
        }

        // When room is closed, mostly after refresh
        if (response.result.room_closed) {
          navigate("/");
        }

        const participantsArray: Participant[] = await Promise.all(
          Object.entries(response.result.participants).map(
            async ([key, username]) => ({
              id: Number(key),
              username: username as string,
              photoUrl: await fetchPhotoUrl(Number(key)),
            })
          )
        );

        setParticipants(participantsArray);
        setOwnerId(response.result.ownerId);
      } catch (e) {
        setError("Błąd podczas pobierania danych pokoju: " + e);
      } finally {
        setIsLoading(false);
      }
    };

    fetchRoomData();
  }, [roomCode]);

  return { participants, ownerId, error, setError, isLoading };
}
