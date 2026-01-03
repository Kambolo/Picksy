export type PollResultsMessage = {
  messageType: "RESULTS";
  polls: PollDTO[];
};

export type PollDTO = {
  pollId: number;
  category: { setId: number; categoryId: number };
  choices: ChoiceDTO[];
  participantsCount: number;
};

type ChoiceDTO = {
  optionId: number;
  count: number;
};
