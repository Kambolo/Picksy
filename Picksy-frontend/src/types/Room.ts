import type { Participant } from "./Participant";

export type Room = {
  roomCode: string;
  name: string;
  categoryIds: number[];
  ownerId: number;
  participants: Participant[];
  createdAt: string;
};
