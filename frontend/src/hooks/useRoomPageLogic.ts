import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  getCategory,
  getCategoryOptions,
  getSetById,
} from "../api/categoryApi";
import { endVoting, nextCategory, startVoting } from "../api/roomApi";
import { useUser } from "../context/userContext";
import type { Option } from "../types/Option";
import type { Participant } from "../types/Participant";
import type { Category } from "../types/Voting";
import { clearState, loadState, saveState } from "../utils/persistState";
import { useNavigationBlocker } from "./useNavigationBloker";
import { useRoomDetails } from "./useRoomDetails";
import useRoomWebSocket from "./useRoomWebsocket";
import fetchPhotoUrl from "./useUserPhotoUrlProvider";
import type { SetInfo } from "../types/Set";

export const useRoomPageLogic = () => {
  const { roomCode } = useParams<{ roomCode: string }>();
  const navigate = useNavigate();
  const { user } = useUser();
  const location = useLocation();
  const categoriesCount = location.state?.categoriesCount;

  const [participant, setParticipant] = useState<Participant | null>(null);
  const [copied, setCopied] = useState(false);
  const [hasJoined, setHasJoined] = useState(false);
  const [isRoomClosed, setIsRoomClosed] = useState(false);
  const [isLeaving, setIsLeaving] = useState(false);
  const [currentCategoryIndex, setCurrentCategoryIndex] = useState(0);

  const [categoryId, setCategoryId] = useState(-1);
  const [currentCategory, setCurrentCategory] = useState<Category | null>(null);
  const [currentSet, setCurrentSet] = useState<Omit<
    SetInfo,
    "categories"
  > | null>(null);
  const [isLoadingCategory, setIsLoadingCategory] = useState(false);
  const [showResults, setShowResults] = useState(false);

  const categoryIdRef = useRef(categoryId);

  const [dynamicParticipants, setDynamicParticipants] = useState<Participant[]>(
    []
  );

  const [isRestoring, setIsRestoring] = useState(true);
  const [, setFirstRender] = useState(true);
  const [finalParticipants, setFinalParticipants] = useState<Participant[]>([]);

  // API: room details
  // It updates the participant list from api whenever roomCode changes
  const {
    participants: apiParticipants,
    ownerId,
    error: roomError,
    setError,
  } = useRoomDetails(roomCode);

  // Every client listen for new participants and check if it isn't already in list from api
  useEffect(() => {
    const idsFromApi = new Set(apiParticipants.map((p) => p.id));
    const merged = [
      ...apiParticipants,
      ...dynamicParticipants.filter((p) => !idsFromApi.has(p.id)),
    ];
    setFinalParticipants(merged);
  }, [apiParticipants, dynamicParticipants]);

  // it ref is used for naming states that are saved to localStorage
  useEffect(() => {
    categoryIdRef.current = categoryId;
  }, [categoryId]);

  // load room state (when page was refreshed)
  useEffect(() => {
    const saved = loadState(`roomState-${roomCode}`);

    if (saved) {
      setParticipant(saved.participant);
      setHasJoined(saved.hasJoined);
      setCategoryId(saved.categoryId);
      setCurrentCategory(saved.currentCategory);
      setShowResults(saved.showResults);
      setIsRoomClosed(saved.isRoomClosed);
      setCurrentCategoryIndex(saved.currentCategoryIndex);
      setFinalParticipants(saved.finalParticipants);
      setCurrentSet(saved.set);
      setFirstRender(false);
    }
    setIsRestoring(false);

    clearState("results");
  }, []);

  // save room state in case of page refresh, use isRestoring
  // to prevent saving default values before loading from
  useEffect(() => {
    if (!isRestoring && hasJoined) {
      saveState(`roomState-${roomCode}`, {
        participant,
        hasJoined,
        categoryId,
        currentCategory,
        showResults,
        isRoomClosed,
        currentCategoryIndex,
        finalParticipants,
        currentSet,
      });
    }
  }, [
    participant,
    hasJoined,
    categoryId,
    currentCategory,
    showResults,
    isRoomClosed,
    currentCategoryIndex,
    finalParticipants,
    currentSet,
  ]);

  // WebSocket
  const {
    isConnected,
    joinRoom,
    leaveRoom,
    error: wsError,
  } = useRoomWebSocket(roomCode, async (message) => {
    switch (message.type) {
      case "JOIN":
        const photoUrl = await fetchPhotoUrl(message.userId);
        setDynamicParticipants((prev) =>
          prev.some((p) => p.id === message.userId)
            ? prev
            : [
                ...prev,
                { id: message.userId, username: message.username, photoUrl },
              ]
        );
        if (participant?.id === message.userId) setHasJoined(true);
        break;

      case "LEAVE":
        setDynamicParticipants((prev) =>
          prev.filter((p) => p.id !== message.userId)
        );
        break;

      case "ROOM_CLOSED":
        clearState(`roomState-${roomCode}`);
        clearState(`votingState-${roomCode}-${categoryIdRef.current}`);
        clearState(`swipeVoting-${roomCode}-${categoryIdRef.current}`);
        clearState(`pickVoting-${roomCode}-${categoryIdRef.current}`);
        setIsRoomClosed(true);
        break;

      case "VOTING_STARTED":
      case "NEXT_CATEGORY":
        if (!message.category) break;
        clearState(`votingState-${roomCode}-${categoryIdRef.current}`);
        clearState(`swipeVoting-${roomCode}-${categoryIdRef.current}`);
        clearState(`pickVoting-${roomCode}-${categoryIdRef.current}`);
        setCategoryId(message.category.categoryId);
        loadCategory(message.category.categoryId);
        if (message.category.setId !== -1) loadSet(message.category.setId);
        else setCurrentSet(null);
        if (message.type === "NEXT_CATEGORY")
          setCurrentCategoryIndex((prev) => prev + 1);
        break;

      case "VOTING_FINISHED":
        clearState(`votingState-${roomCode}-${categoryIdRef.current}`);
        clearState(`swipeVoting-${roomCode}-${categoryIdRef.current}`);
        clearState(`pickVoting-${roomCode}-${categoryIdRef.current}`);

        setCategoryId(-1);
        setCurrentCategory(null);

        // get voting results
        if (!roomCode) {
          setError("Wystapił błąd");
          break;
        }
        setShowResults(true);
        break;
    }
  });

  // leave room
  const handleLeaveRoom = () => {
    if (isConnected && hasJoined && participant?.id)
      leaveRoom(participant.id, participant.username);
    clearState(`roomState-${roomCode}`); //clear room state
    setHasJoined(false);
    setParticipant(null);
    setIsLeaving(true);
    setTimeout(() => navigate("/"), 20);
  };

  const handleStartVoting = async () => {
    if (!roomCode) return setError("Rozpoczęcie głosowania nie powiodło się");
    const response = await startVoting(roomCode);
    if (response.status !== 200)
      setError("Rozpoczęcie głosowania nie powiodło się");
  };

  const handleNextCategory = async () => {
    if (!roomCode || !user) return;
    const response = await nextCategory(roomCode);
    if (response.status !== 200)
      setError("Błąd podczas przechodzenia do kolejnej kategorii");
  };

  const handleEndVoting = async () => {
    if (!roomCode || !user) return;
    const response = await endVoting(roomCode);
    if (response.status !== 200) setError("Błąd podczas kończenia głosowania");
  };

  const handleCopyCode = () => {
    if (!roomCode) return;
    navigator.clipboard.writeText(roomCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const shouldBlock = () => {
    if (isRoomClosed || isLeaving) return false;
    return isConnected && hasJoined && !!participant?.id;
  };

  const { isBlocked, confirmNavigation, cancelNavigation } =
    useNavigationBlocker(
      shouldBlock,
      [isConnected, hasJoined, participant, isRoomClosed, isLeaving],
      () => {
        if (isConnected && hasJoined && participant?.id) {
          leaveRoom(participant.id, participant.username);
        }

        clearState(`roomState-${roomCode}`); //clear room state on leave
        clearState(`votingState-${roomCode}-${categoryIdRef.current}`);
        clearState(`swipeVoting-${roomCode}-${categoryIdRef.current}`);
      }
    );

  const loadCategory = async (catId: number) => {
    setIsLoadingCategory(true);
    const categoryResponse = await getCategory(catId);
    const optionsResponse = await getCategoryOptions(catId);
    if (categoryResponse.status === 200 && optionsResponse.status === 200) {
      const options: Option[] = optionsResponse.result.map((opt: Option) => ({
        ...opt,
        photoURL:
          opt.photoURL ||
          "https://res.cloudinary.com/dctiucda1/image/upload/v1760881210/image_oetxkk.png",
      }));
      setCurrentCategory({
        ...categoryResponse.result,
        options,
      });
    } else setError("Błąd podczas wczytywania kategorii.");
    setIsLoadingCategory(false);
  };

  const loadSet = async (setId: number) => {
    if (currentSet && currentSet.id === setId) return;
    setIsLoadingCategory(true);
    const response = await getSetById(setId);
    if (response.status === 200) {
      setCurrentSet({
        id: response.result.id,
        title: response.result.name,
        author: "Picksy",
        authorId: response.result.authorId,
        categoryCount: response.result.categories.length,
        isPublic: response.result.isPublic,
        views: response.result.views,
        showIsPublic: false,
      });
    } else {
      setError("Błąd podczas wczytywania zestawu.");
    }
  };

  // join on load and only if it is the first render skip refresh
  useEffect(() => {
    if (isRestoring) return;
    if (!hasJoined && isConnected) {
      let username = location.state?.username || "Guest";
      let id = location.state?.id || user?.id || null;
      if (user) username = user.username;
      setParticipant({ id, username });
      const timeout = setTimeout(() => {
        joinRoom(username, id);
        setHasJoined(true);
      }, 20);
      return () => clearTimeout(timeout);
    }
  }, [isConnected, isRestoring, hasJoined]);

  return {
    roomCode,
    wsError,
    isRoomClosed,
    categoryId,
    currentCategory,
    isLoadingCategory,
    showResults,
    isBlocked,
    confirmNavigation,
    cancelNavigation,

    votingProps: {
      category: currentCategory,
      set: currentSet,
      roomCode,
      isOwner: participant?.id === ownerId,
      participantsCount: finalParticipants.length,
      onNextCategory: handleNextCategory,
      onEndVoting: handleEndVoting,
      categoriesCount,
      currentCategoryIndex,
    },

    waitingRoomProps: {
      roomCode: roomCode || "",
      copied,
      onCopy: handleCopyCode,
      onLeave: handleLeaveRoom,
      participants: finalParticipants,
      participant,
      ownerId,
      isConnected,
      onStartVoting: handleStartVoting,
      roomError,
    },
  };
};
