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

interface useRoomWebsocketReturn {
  isConnected: boolean;
  error: string | null;
  setError: Dispatch<SetStateAction<string | null>>;
  joinRoom: (username: string, id: number | null) => void;
  leaveRoom: (userId: number, username: string) => void;
}

const useRoomWebsocket = (
  roomCode: string | undefined,
  onMessage?: (message: RoomMessage) => void
): useRoomWebsocketReturn => {
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const [message, setMessage] = useState<string>("");
  const stompClientRef = useRef<Client | null>(null);

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

  const joinRoom = (username: string, id: number | null) => {
    const stompClient = stompClientRef.current;
    if (stompClient && stompClient.connected) {
      const message: RoomMessage = {
        userId: id,
        username: username,
        type: "JOIN",
      };
      stompClient.publish({
        destination: "/app/public/room/" + roomCode + "/join",
        body: JSON.stringify(message),
      });
    } else {
      console.error("Stomp client is not connected");
      //setError("Nie mozna dołączyć do pokoju");
    }
  };

  const leaveRoom = (userId: number, username: string) => {
    const stompClient = stompClientRef.current;
    if (!stompClient || !isConnected) {
      return;
    }

    const message: RoomMessage = {
      userId,
      username,
      type: "LEAVE",
    };

    if (stompClient && stompClient.connected) {
      const message: RoomMessage = {
        userId,
        username,
        type: "LEAVE",
      };
      stompClient.publish({
        destination: "/app/public/room/" + roomCode + "/leave",
        body: JSON.stringify(message),
      });
    } else {
      console.error("Stomp client is not connected");
      //setError("Nie mozna dołączyć do pokoju");
    }
  };

  return {
    isConnected,
    error,
    setError,
    joinRoom,
    leaveRoom,
  };
};

export default useRoomWebsocket;
