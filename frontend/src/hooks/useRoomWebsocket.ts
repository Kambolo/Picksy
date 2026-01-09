import { Client } from "@stomp/stompjs";
import {
  useEffect,
  useRef,
  useState,
  type Dispatch,
  type SetStateAction,
} from "react";
import SockJS from "sockjs-client";
import type { RoomMessage } from "../types/RoomMessage";
import { useUser } from "../context/userContext";
import { leaveRoom } from "../api/roomApi";

interface useRoomWebsocketReturn {
  isConnected: boolean;
  error: string | null;
  setError: Dispatch<SetStateAction<string | null>>;
}

const useRoomWebsocket = (
  roomCode: string | undefined,
  onMessage?: (message: RoomMessage) => void
): useRoomWebsocketReturn => {
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const stompClientRef = useRef<Client | null>(null);
  const { user, setUser } = useUser();

  useEffect(() => {
    if (!roomCode) {
      setError("Nie podano kodu pokoju");
      return;
    }
    const socket = new SockJS("http://localhost:8080/ws-room");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        setIsConnected(true);
        stompClient.subscribe("/topic/room/" + roomCode, (response) => {
          const responseJson: RoomMessage = JSON.parse(response.body);
          if (onMessage) onMessage(responseJson);
        });
      },
      onDisconnect: () => {
        leaveRoom(roomCode || "", user?.id || -1, user?.username || ""); // notify server about disconnection
        user && user.id < 0 && setUser(null);
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame);
        setError(frame.headers["message"] || "Błąd połączenia");
        setIsConnected(false);
      },
      onWebSocketError: (event) => {
        console.error("WebSocket error:", event);
        setError("Błąd WebSocket");
      },
    });

    stompClient.activate();
    stompClientRef.current = stompClient;

    return () => {
      stompClient.deactivate();
    };
  }, []);

  return {
    isConnected,
    error,
    setError,
  };
};

export default useRoomWebsocket;
