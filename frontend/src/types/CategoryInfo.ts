import type { CategoryType } from "./CategoryDetails";

export type CategoryInfo = {
  id: number;
  name: string;
  authorID: number;
  author: string;
  type: CategoryType;
  photoURL: string;
  description: string | null;
  views: number;
  created: Date;
  isPublic: boolean;
};
