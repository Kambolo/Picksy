export interface ResultCategory {
  id: number;
  setId: number;
  setTitle: string;
  name: string;
  options: Choice[];
  participantsCount: number;
}

export type Choice = {
  id: number;
  name: string;
  count: number;
};
