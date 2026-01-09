import { useEffect, useState } from "react";
import { getCategoryWithOptions, getSetById } from "../api/categoryApi";
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
        console.log(results);

        const responses = await Promise.all(
          results.map(async (r: PollDTO) => {
            const categoryResponse = await getCategoryWithOptions(
              r.category.categoryId
            );
            if (categoryResponse.status !== 200) {
              setError("Błąd podczas pobierania danych kategorii");
              setLoading(false);
              return null;
            }

            const setResponse =
              r.category.setId !== -1
                ? await getSetById(r.category.setId)
                : {
                    status: 200,
                    result: {
                      id: -1,
                      name: "",
                    },
                  };

            if (setResponse.status !== 200) {
              setError("Błąd podczas pobierania danych kategorii");
              setLoading(false);
              return;
            }

            return { ...categoryResponse.result, ...setResponse.result };
          })
        );
        console.log(responses);

        const mapped: ResultCategory[] = responses.map((response) => {
          if (!response) return null;

          const categoryDTO = response.categoryDTO;
          const optionDTOs = response.optionDTOs;

          const choices = results.find(
            (r) => r.category.categoryId === categoryDTO.id
          )?.choices;

          const participantsCount = results.find(
            (r) => r.category.categoryId === categoryDTO.id
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
            setId: response.id,
            setTitle: response.name,
          };
        });

        setCategories(mapped);
      } catch (e: any) {
        setError("Wystąpił błąd podczas pobierania wyników: " + e.message);
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  return { error, categories, loading };
};
