import { useEffect, useState } from "react";
import { getCategoryWithOptions } from "../api/categoryApi";
import { getResults } from "../api/roomApi";
import type { Choice, ResultCategory } from "../types/Choice";
import type { Option } from "../types/Option";
import type { PollDTO } from "../types/PollResultsMessage";

type useResultsReturn = {
  error: string;
  categories: ResultCategory[];
  loading: boolean;
};

export const useResults = ({
  roomCode,
}: {
  roomCode: string;
}): useResultsReturn => {
  const [categories, setCategories] = useState<ResultCategory[]>([]);
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const resultResponse = await getResults(roomCode);

        if (resultResponse.status !== 200) {
          setError("Błąd podczas pobierania danych kategorii");
          setLoading(false);
          return;
        }

        const results: PollDTO[] = resultResponse.result;

        const responses = await Promise.all(
          results.map((r: PollDTO) => getCategoryWithOptions(r.categoryId))
        );

        if (!responses.every((r) => r.status === 200)) {
          setError("Błąd podczas pobierania danych kategorii");
          setLoading(false);
          return;
        }

        const mapped: ResultCategory[] = responses.map((response) => {
          const categoryDTO = response.result.categoryDTO;
          const optionDTOs = response.result.optionDTOs;

          const choices = results.find(
            (r) => r.categoryId === categoryDTO.id
          )?.choices;

          const participantsCount = results.find(
            (r) => r.categoryId === categoryDTO.id
          )?.participantsCount;

          let options: Choice[] = [];

          if (choices) {
            options = optionDTOs.map((option: Option) => ({
              ...option,
              count: choices.find((choice) => choice.optionId === option.id)
                ?.count,
            }));
          }
          return {
            ...categoryDTO,
            options,
            participantsCount,
          };
        });

        setCategories(mapped);
      } catch (e: any) {
        setError("Nie udało się pobrać wyników");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  return { error, categories, loading };
};
