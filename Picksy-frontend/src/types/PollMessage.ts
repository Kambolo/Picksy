type MessageType =
  | "SETUP"
  | "START"
  | "END"
  | "VOTE"
  | "UPDATE_PARTICIPANT_COUNT"
  | "MATCH"
  | "INCREASE_VOTED_COUNT";

export type PollMessage = {
  optionsId: number[] | null;
  messageType: MessageType;
  participantsCount: number;
};
