import type { CategoryType } from "./CategoryDetails";
import type { Option } from "./Option";

export const VotingType = {
  PICK: "PICK",
  SWIPE: "SWIPE",
} as const;

export interface Vote {
  optionId: number;
  userId: string;
  timestamp: number;
}

export interface VotingSession {
  categoryId: number;
  votes: Vote[];
  isActive: boolean;
}

export interface Category {
  id: number;
  name: string;
  type: CategoryType;
  options: Option[] | null;
  photoURL: string;
  description: string;
  authorID: number;
  views: number;
  created: string;
}
