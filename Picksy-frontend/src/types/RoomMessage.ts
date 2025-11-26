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
  categoryId?: number;
  type: MessageType;
};
