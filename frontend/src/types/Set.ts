import type { CategoryCardProps } from "../components/CategoryCard/CategoryCard";

export type SetInfo = {
  id: number;
  title: string;
  author: string;
  authorId: number;
  isPublic: boolean;
  views: number;
  showIsPublic: boolean;
  categories: CategoryCardProps[];
};
