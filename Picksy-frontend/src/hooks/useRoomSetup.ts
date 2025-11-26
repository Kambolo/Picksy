import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createRoom } from "../api/roomApi";
import { useCategories } from "./useCategories";
import { useUser } from "../context/userContext";

export const useRoomSetup = () => {
  const { user } = useUser();
  const navigate = useNavigate();

  // roomName z localStorage
  const savedRoomName = localStorage.getItem("roomName") ?? "";
  const [roomName, setRoomNameState] = useState<string>(savedRoomName);
  const [error, setError] = useState<string>("");

  // categories hook
  const { categories, addCategory, removeCategory, clearCategories } =
    useCategories();

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
      const categoryIds = categories.map((c) => c.id);
      const response = await createRoom(roomName, categoryIds);

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
      setError("Wystąpił nieoczekiwany błąd");
    }
  };

  return {
    roomName,
    setRoomName,
    categories,
    addCategory,
    removeCategory,
    clearCategories,
    handleCreateRoom,
    error,
  };
};
