import React from "react";
import Navbar from "../Navbar/Navbar";
import { CategoryHeader } from "../CategoryHeader/CategoryHeader";
import { PickVoting } from "../PickVoting/PickVoting";
import { SwipeVoting } from "../SwipeVoting/SwipeVoting";
import { Loading } from "../Loading/Loading";
import { Error } from "../Error/Error";
import { FaUserGroup } from "react-icons/fa6";
import { VotingType, type Category } from "../../types/Voting";
import { useVotingLogic } from "../../hooks/useVotingLogic";
import "./Voting.css";

type VotingProps = {
  category: Category | null;
  roomCode: string | undefined;
  isOwner: boolean;
  participantsCount: number;
  categoriesCount: number;
  currentCategory: number;
  onNextCategory: () => void;
  onEndVoting: () => void;
};

export const Voting: React.FC<VotingProps> = ({
  category,
  roomCode,
  isOwner,
  participantsCount,
  categoriesCount,
  currentCategory,
  onNextCategory,
  onEndVoting,
}) => {
  if (!category)
    return (
      <Error error="Brak kategorii" isRoomClosed={false} showResults={false} />
    );
  if (!roomCode)
    return (
      <Error
        error="Brak kodu pokoju"
        isRoomClosed={false}
        showResults={false}
      />
    );

  const {
    error,
    hasStarted,
    hasVoted,
    setHasOptionsEnded,
    hasOptionsEnded,
    matchedId,
    votedCount,
    handleVote,
    // eslint-disable-next-line react-hooks/rules-of-hooks
  } = useVotingLogic(
    roomCode,
    category,
    isOwner,
    participantsCount,
    currentCategory
  );

  if (error)
    return <Error error={error} isRoomClosed={false} showResults={false} />;
  if (!hasStarted) {
    console.log("huj dupa drabina");
    return <Loading />;
  }

  return (
    <div>
      <Navbar />
      <div className="voting-page">
        {/* Header */}
        <div className="voting-header">
          <div className="room-info-container">
            <span className="room-code">Pokój: {roomCode}</span>
            {isOwner && <span className="owner-badge">Właściciel</span>}
          </div>
          <div className="btn-container">
            {isOwner &&
              onNextCategory &&
              currentCategory < categoriesCount - 1 && (
                <button
                  className="control-btn next-btn header-btn-next"
                  onClick={onNextCategory}
                >
                  →
                </button>
              )}
            {isOwner && (
              <button
                className="control-btn end-btn header-btn"
                onClick={onEndVoting}
              >
                Zakończ głosowanie
              </button>
            )}
          </div>
        </div>
        {/* CATEGORY HEADER */}
        <div className="voting-category-header">
          <div className="voted-counter-container">
            <div>
              <FaUserGroup size={32} color="#7a7568" />
              <h2>
                {votedCount}/{participantsCount}
              </h2>
            </div>
          </div>

          <CategoryHeader
            img={category.photoURL}
            title={category.name}
            description={category.description}
            type={category.type}
          />
        </div>
        {/* VOTING COMPONENT */}
        <div className="voting-content">
          {category.type === VotingType.PICK ? (
            <PickVoting
              options={category.options || []}
              onVote={handleVote}
              hasVoted={hasVoted}
              categoryId={category.id}
              roomCode={roomCode}
            />
          ) : (
            <SwipeVoting
              options={category.options || []}
              onVote={handleVote}
              hasOptionsEnded={hasOptionsEnded}
              setHasOptionsEnded={setHasOptionsEnded}
              matchedId={matchedId}
              categoryId={category.id}
              roomCode={roomCode}
            />
          )}
        </div>
        {/* Owner Controls */}
        {isOwner && (hasOptionsEnded || hasVoted) && (
          <div className="owner-controls">
            <div className="controls-buttons">
              {onNextCategory && currentCategory < categoriesCount - 1 && (
                <button
                  className="control-btn next-btn"
                  onClick={onNextCategory}
                >
                  Następna Kategoria →
                </button>
              )}

              <button className="control-btn end-btn" onClick={onEndVoting}>
                Zakończ głosowanie
              </button>
            </div>
          </div>
        )}
        {/* NON-OWNER WAITING */}
        {!isOwner && hasVoted && (
          <div className="waiting-message">
            <div className="waiting-icon">⏳</div>
            <p>Oczekiwanie na kontynuację...</p>
          </div>
        )}
      </div>
    </div>
  );
};
