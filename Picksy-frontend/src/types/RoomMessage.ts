export type MessageType =
  | "JOIN"
  | "LEAVE"
  | "ROOM_CLOSED"
  | "VOTING_STARTED"
  | "NEXT_CATEGORY"
  | "VOTING_FINISHED";

export type RoomMessage = {
  userId: number | null;
  username: string;
  category?: { setId: number; categoryId: number };
  type: MessageType;
};
