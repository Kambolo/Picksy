import type { Participant } from "./Participant";

export type Room = {
  roomCode: string;
  name: string;
  categorySets: { setId: number; categoryId: number }[];
  ownerId: number;
  participants: Participant[];
  createdAt: string;
};
