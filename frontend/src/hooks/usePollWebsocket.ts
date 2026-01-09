import { Client } from "@stomp/stompjs";
import {
  useEffect,
  useRef,
  useState,
  type Dispatch,
  type SetStateAction,
} from "react";
import SockJS from "sockjs-client";
import type { PollMessage } from "../types/PollMessage";
import type { PollResultsMessage } from "../types/PollResultsMessage";
import { useUser } from "../context/userContext";

interface usePollWebsocketReturn {
  isConnected: boolean;
  error: string | null;
  setError: Dispatch<SetStateAction<string | null>>;
  setup: (participantsCount: number) => void;
  vote: (optionsId: number[]) => void;
  end: () => void;
  updateParticipanCount: (participantsCount: number) => void;
  increaseVotedCount: () => void;
}

const usePollWebsocket = (
  roomCode: string | undefined,
  categoryId: number | undefined,
  onMessage?: (message: PollMessage | PollResultsMessage) => void
): usePollWebsocketReturn => {
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const stompClientRef = useRef<Client | null>(null);
  const { user } = useUser();

  useEffect(() => {
    if (!roomCode || !categoryId) {
      setError("Nie podano kodu pokoju lub id kategori");
      return;
    }
    const socket = new SockJS("http://localhost:8080/ws-poll");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        setIsConnected(true);
        stompClient.subscribe(
          "/topic/poll/" + roomCode + "/" + categoryId,
          (response) => {
            const responseJson: PollMessage | PollResultsMessage = JSON.parse(
              response.body
            );
            if (onMessage) onMessage(responseJson);
          }
        );
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

  const setup = (participantsCount: number) => {
    
    const message: PollMessage = {
      userId: user && user.id ? user.id : -1,
      optionsId: [],
      messageType: "SETUP",
      participantsCount: participantsCount,
    };
    sendMessage(message);
  };

  const vote = (optionsId: number[]) => {
    const message: PollMessage = {
      userId: user && user.id ? user.id : -1,
      optionsId,
      messageType: "VOTE",
      participantsCount: 0,
    };
    sendMessage(message);
  };

  const updateParticipanCount = (participantsCount: number) => {
    const message: PollMessage = {
      userId: user && user.id ? user.id : -1,
      optionsId: [],
      messageType: "UPDATE_PARTICIPANT_COUNT",
      participantsCount: participantsCount,
    };
    sendMessage(message);
  };

  const increaseVotedCount = () => {
    const message: PollMessage = {
      userId: user && user.id ? user.id : -1,
      optionsId: [],
      messageType: "INCREASE_VOTED_COUNT",
      participantsCount: -1,
    };

    sendMessage(message);
  };

  const end = () => {
    const message: PollMessage = {
      userId: user && user.id ? user.id : -1,
      optionsId: [],
      messageType: "END",
      participantsCount: 0,
    };
    sendMessage(message);
  };

  const sendMessage = (message: PollMessage) => {
    const stompClient = stompClientRef.current;
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: `/app/poll/${roomCode}/${categoryId}`,
        body: JSON.stringify(message),
      });
    } else {
      console.error("Stomp client is not connected");
      //setError("Nie mozna dołączyć do głosowania");
    }
  };

  return {
    isConnected,
    error,
    setError,
    setup,
    vote,
    end,
    updateParticipanCount,
    increaseVotedCount,
  };
};

export default usePollWebsocket;
