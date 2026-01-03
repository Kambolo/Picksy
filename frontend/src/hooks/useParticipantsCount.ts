import { useEffect, useState } from "react";
import { getParticipantsCount } from "../api/roomApi";

export const useParticipantsCount = (roomCode: string) => {
  const [participants, setParticipants] = useState<number | null>(null);

  useEffect(() => {
    const fetchCount = async () => {
      const response = await getParticipantsCount(roomCode);

      if (response.status === 200) {
        setParticipants(response.result);
      } else {
        setParticipants(-1); // błąd
      }
    };

    fetchCount();
    console.log(roomCode);
  }, [roomCode]);

  return { participants };
};
