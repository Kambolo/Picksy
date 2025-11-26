import type { Option } from "./Option";

export type CategoryType = "PICK" | "SWIPE";

export type CategoryDetails = {
  id: number;
  name: string;
  type: CategoryType;
  options: Option[];
  author: string;
  authorID?: number;
  photoURL: string;
  description: string | null;
  views: number;
  created: Date;
  isPublic?: boolean;
};
