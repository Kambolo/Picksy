import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createRoom } from "../api/roomApi";
import { useUser } from "../context/userContext";
import { useCategories } from "./useCategories";

export const useRoomSetup = () => {
  const { user } = useUser();
  const navigate = useNavigate();

  // roomName z localStorage
  const savedRoomName = localStorage.getItem("roomName") ?? "";
  const [roomName, setRoomNameState] = useState<string>(savedRoomName);
  const [error, setError] = useState<string>("");

  // categories hook
  const {
    categories,
    addCategory,
    removeCategory,
    sets,
    removeSet,
    clearCategories,
  } = useCategories();

  // setter dla roomName z lokalStorage
  const setRoomName = (value: string) => {
    setRoomNameState(value);
    localStorage.setItem("roomName", value);
  };

  const handleCreateRoom = async () => {
    if (!roomName.trim()) {
      setError("Podaj nazwę pokoju");
      return;
    }
    if (categories.length === 0) {
      setError("Wybierz przynajmniej jedną kategorię");
      return;
    }
    if (!user) {
      setError("Zaloguj się, aby utworzyć pokój");
      return;
    }

    try {
      const categorySet = categories.map((c) => ({
        setId: c.set ? c.set?.id : -1,
        categoryId: c.id,
      }));
      const response = await createRoom(roomName, categorySet);

      if (response.status === 200) {
        const roomCode = response.result.roomCode;
        navigate("/room/" + roomCode, {
          state: {
            categoriesCount: categories.length,
          },
        });
      } else {
        setError("Wystąpił błąd przy tworzeniu pokoju");
      }
    } catch (e) {
      setError("Wystąpił nieoczekiwany błąd: " + e);
    }
  };

  return {
    roomName,
    setRoomName,
    categories,
    addCategory,
    removeCategory,
    sets,
    removeSet,
    clearCategories,
    handleCreateRoom,
    error,
  };
};
