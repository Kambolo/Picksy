import { useEffect, useRef, useState } from "react";
import type { PollMessage } from "../types/PollMessage";
import type { PollResultsMessage } from "../types/PollResultsMessage";
import type { Category } from "../types/Voting";
import { loadState, saveState } from "../utils/persistState";
import usePollWebsocket from "./usePollWebsocket";

export const useVotingLogic = (
  roomCode: string,
  category: Category,
  isOwner: boolean,
  participantsCount: number,
  currentCategory: number,
  nextCategory: () => void,
  categoriesCount: number,
  endVoting: () => void
) => {
  const [hasVoted, setHasVoted] = useState(false);
  const [hasStarted, setHasStarted] = useState(false);

  const [matchedId, setMatchedId] = useState(-1);
  const [hasOptionsEnded, setHasOptionsEnded] = useState(false);
  const [votedCount, setVotedCount] = useState(0);

  const [loaded, setLoaded] = useState(false);

  const firstRenderRef = useRef(true);
  const prevCategoryRef = useRef(currentCategory);

  // load state
  useEffect(() => {
    const saved = loadState(`votingState-${roomCode}-${category.id}`);
    if (saved) {
      setHasVoted(saved.hasVoted);
      setHasStarted(saved.hasStarted);
      setMatchedId(saved.matchedId);
      setHasOptionsEnded(saved.hasOptionsEnded);
      setVotedCount(saved.votedCount);
    }
    setLoaded(true);
  }, []);

  //save state
  useEffect(() => {
    if (!loaded) return;
    saveState(`votingState-${roomCode}-${category.id}`, {
      hasVoted,
      hasStarted,
      matchedId,
      hasOptionsEnded,
      votedCount,
    });
  }, [hasVoted, hasStarted, matchedId, hasOptionsEnded, votedCount]);

  // go to next category if everyone voted
  useEffect(() => {
    if (isOwner && hasStarted && participantsCount === votedCount) {
      if (currentCategory < categoriesCount - 1) nextCategory();
      else endVoting();
    }
  }, [votedCount, participantsCount, isOwner, hasStarted, nextCategory]);

  const {
    isConnected,
    error,
    setup,
    vote,
    updateParticipanCount,
    increaseVotedCount,
  } = usePollWebsocket(
    roomCode,
    category.id,
    async (message: PollMessage | PollResultsMessage) => {
      switch (message.messageType) {
        case "START":
          setHasStarted(true);
          console.log("Voting started");
          break;
        case "MATCH":
          setMatchedId(message.optionsId?.[0] ?? -1);
          break;
        case "INCREASE_VOTED_COUNT":
          setVotedCount(message.optionsId?.[0] ?? 0);
          break;
      }
    }
  );

  const handleVote = (
    optionIds: number[],
    right?: boolean,
    isLast?: boolean
  ) => {
    if (category.type === "PICK") {
      setHasVoted(true);
      increaseVotedCount();
      vote(optionIds);
    } else {
      if (right) vote(optionIds);
      if (isLast) {
        increaseVotedCount();
        setHasVoted(true);
      }
    }
  };

  useEffect(() => {
    updateParticipanCount(participantsCount);
  }, [participantsCount]);

  useEffect(() => {
    if (firstRenderRef.current) {
      firstRenderRef.current = false;
      prevCategoryRef.current = currentCategory;
      return; // skip first run (initial load)
    }

    if (currentCategory !== prevCategoryRef.current) {
      setHasVoted(false); // reset on category change
      prevCategoryRef.current = currentCategory;
    }
  }, [currentCategory]);

  useEffect(() => {
    if (!isConnected) return;
    if (!hasStarted && isOwner) setup(participantsCount);
  }, [isConnected, hasStarted]);

  return {
    error,
    hasStarted,
    hasVoted,
    setHasOptionsEnded,
    hasOptionsEnded,
    matchedId,
    votedCount,
    handleVote,
  };
};
