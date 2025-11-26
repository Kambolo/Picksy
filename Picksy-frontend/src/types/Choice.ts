export interface ResultCategory {
  id: number;
  name: string;
  options: Choice[];
  participantsCount: number;
}

export type Choice = {
  id: number;
  name: string;
  count: number;
};
