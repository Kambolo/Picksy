// SwipeVoting.tsx
import React, {
  useEffect,
  useRef,
  useState,
  type Dispatch,
  type SetStateAction,
} from "react";
import type { Option } from "../../types/Option";
import { loadState, saveState } from "../../utils/persistState";
import "./SwipeVoting.css";

interface SwipeVotingProps {
  options: Option[];
  onVote: (selectedOptions: number[], right: boolean, isLast: boolean) => void;
  hasOptionsEnded: boolean;
  setHasOptionsEnded: Dispatch<SetStateAction<boolean>>;
  matchedId: number;
  categoryId: number;
  roomCode: string;
}

export const SwipeVoting: React.FC<SwipeVotingProps> = ({
  options,
  onVote,
  hasOptionsEnded,
  setHasOptionsEnded,
  matchedId,
  categoryId,
  roomCode,
}) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  const [liked, setLiked] = useState<Set<number>>(new Set());
  const [swipeDirection, setSwipeDirection] = useState<"left" | "right" | null>(
    null
  );
  const [showMatch, setShowMatch] = useState(false);
  const cardRef = useRef<HTMLDivElement>(null);
  const [dragStart, setDragStart] = useState<{ x: number; y: number } | null>(
    null
  );
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

  const [isLoaded, setIsLoaded] = useState(false);

  //load state
  useEffect(() => {
    const saved = loadState(`swipeVoting-${roomCode}-${categoryId}`);
    if (saved) {
      setCurrentIndex(saved.currentIndex);
      setLiked(new Set(saved.liked));
      setHasOptionsEnded(saved.hasOptionsEnded);
    }
    setIsLoaded(true);
  }, []);

  //save state
  useEffect(() => {
    if (!isLoaded) return;
    saveState(`swipeVoting-${roomCode}-${categoryId}`, {
      currentIndex,
      liked: Array.from(liked),
      hasOptionsEnded,
    });
  }, [currentIndex, liked, hasOptionsEnded]);

  const currentOption = options[currentIndex];
  const isLastCard = currentIndex === options.length - 1;
  const matchedOption = options.find((opt) => opt.id === matchedId);

  useEffect(() => {
    if (currentIndex === options.length) setHasOptionsEnded(true);
  }, [currentIndex, options.length, setHasOptionsEnded]);

  useEffect(() => {
    if (matchedId > -1) {
      setShowMatch(true);
    }
  }, [matchedId]);

  const handleSwipe = (direction: "left" | "right") => {
    setSwipeDirection(direction);

    // For counting right swipes
    if (direction === "right") {
      setLiked(new Set([...liked, currentOption.id]));
    }

    setTimeout(() => {
      setCurrentIndex(currentIndex + 1);
      setSwipeDirection(null);
      setDragOffset({ x: 0, y: 0 });

      onVote(
        [currentOption.id],
        direction === "right" ? true : false,
        currentIndex === options.length - 1
      );
      if (isLastCard) setHasOptionsEnded(true);
    }, 300);
  };

  const handleMouseDown = (e: React.MouseEvent) => {
    if (hasOptionsEnded) return;
    setDragStart({ x: e.clientX, y: e.clientY });
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!dragStart || hasOptionsEnded) return;
    const deltaX = e.clientX - dragStart.x;
    const deltaY = e.clientY - dragStart.y;
    setDragOffset({ x: deltaX, y: deltaY });
  };

  const handleMouseUp = () => {
    if (!dragStart || hasOptionsEnded) return;

    if (Math.abs(dragOffset.x) > 100) {
      handleSwipe(dragOffset.x > 0 ? "right" : "left");
    } else {
      setDragOffset({ x: 0, y: 0 });
    }
    setDragStart(null);
  };

  const handleTouchStart = (e: React.TouchEvent) => {
    if (hasOptionsEnded) return;
    const touch = e.touches[0];
    setDragStart({ x: touch.clientX, y: touch.clientY });
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    if (!dragStart || hasOptionsEnded) return;
    const touch = e.touches[0];
    const deltaX = touch.clientX - dragStart.x;
    const deltaY = touch.clientY - dragStart.y;
    setDragOffset({ x: deltaX, y: deltaY });
  };

  const handleTouchEnd = () => {
    if (!dragStart || hasOptionsEnded) return;

    if (Math.abs(dragOffset.x) > 100) {
      handleSwipe(dragOffset.x > 0 ? "right" : "left");
    } else {
      setDragOffset({ x: 0, y: 0 });
    }
    setDragStart(null);
  };

  // Show match notification
  if (showMatch && matchedOption) {
    return (
      <div className="swipe-voting">
        <div className="match-overlay" onClick={() => setShowMatch(false)}>
          <h1 className="match-text">MATCH</h1>
          <div className="match-card">
            <div className="match-card-image">
              {matchedOption.photoURL ? (
                <img
                  src={matchedOption.photoURL}
                  alt={matchedOption.name}
                  loading="lazy"
                  referrerPolicy="no-referrer"
                />
              ) : (
                <div className="no-image-placeholder">No Image</div>
              )}
            </div>
            <div className="match-card-info">
              <h3>{matchedOption.name}</h3>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!currentOption && hasOptionsEnded) {
    return (
      <div className="swipe-voting">
        <div className="voting-complete">
          <div className="complete-icon">✓</div>
          <h3>Głosowanie zakończone!</h3>
          <p>
            Polubiłeś {liked.size} z {options.length} opcji
          </p>
        </div>
      </div>
    );
  }

  if (!currentOption) return null;

  const rotation = dragOffset.x * 0.1;
  const opacity = 1 - Math.abs(dragOffset.x) / 300;

  return (
    <div className="swipe-voting">
      <div className="swipe-container">
        <div className="cards-stack">
          {/* Next card preview */}
          {currentIndex < options.length - 1 && (
            <div className="card-preview">
              <div className="card-preview-image">
                {options[currentIndex + 1].photoURL && (
                  <img src={options[currentIndex + 1].photoURL} alt="" />
                )}
              </div>
            </div>
          )}

          {/* Current card */}
          <div
            ref={cardRef}
            className={`swipe-card ${
              swipeDirection ? `swiping-${swipeDirection}` : ""
            }`}
            style={{
              transform: `translateX(${dragOffset.x}px) translateY(${dragOffset.y}px) rotate(${rotation}deg)`,
              opacity: opacity,
            }}
            onMouseDown={handleMouseDown}
            onMouseMove={handleMouseMove}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
            onTouchStart={handleTouchStart}
            onTouchMove={handleTouchMove}
            onTouchEnd={handleTouchEnd}
          >
            <div className="swipe-card-image">
              {currentOption.photoURL ? (
                <img src={currentOption.photoURL} alt={currentOption.name} />
              ) : (
                <div className="no-image-placeholder">No Image</div>
              )}
            </div>
            <div className="swipe-card-info">
              <h3>{currentOption.name}</h3>
              <div className="card-counter">
                {currentIndex + 1} / {options.length}
              </div>
            </div>

            {/* Swipe indicators */}
            <div
              className={`swipe-indicator left ${
                dragOffset.x < -50 ? "active" : ""
              }`}
            >
              NOPE
            </div>
            <div
              className={`swipe-indicator right ${
                dragOffset.x > 50 ? "active" : ""
              }`}
            >
              LIKE
            </div>
          </div>
        </div>

        {/* Action buttons */}
        {!hasOptionsEnded && (
          <div className="swipe-actions">
            <button
              className="swipe-btn dislike"
              onClick={() => handleSwipe("left")}
            >
              ✕
            </button>
            <button
              className="swipe-btn like"
              onClick={() => handleSwipe("right")}
            >
              ♥
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
