import { useState, useCallback, useEffect, useRef } from "react";
import debounce from "lodash.debounce";
import type { SortOption } from "../components/PageNavigation/PageNavigation";

const SORT_MAPPING: Record<SortOption, { field: string; ascending: boolean }> =
  {
    views: { field: "views", ascending: false },
    newest: { field: "created", ascending: false },
    oldest: { field: "created", ascending: true },
    alphabetical: { field: "name", ascending: true },
  };

export const usePaginationAndSort = () => {
  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);

  // Sorting state
  const [currentSortBy, setCurrentSortBy] = useState<SortOption>("views");
  const [sortByForApi, setSortByForApi] = useState("views");
  const [ascending, setAscending] = useState(false);

  // Search state
  const [searchValue, setSearchValue] = useState("");
  const [debouncedSearchValue, setDebouncedSearchValue] = useState("");

  const [activeView, setActiveView] = useState<"kategorie" | "zestawy">(
    "kategorie"
  );

  // Handle sort change
  const handleSortChange = (sort: SortOption) => {
    const sortConfig = SORT_MAPPING[sort];

    setSortByForApi(sortConfig.field);
    setAscending(sortConfig.ascending);
    setCurrentSortBy(sort);
    setCurrentPage(1);
  };

  const debouncedUpdateRef = useRef(
    debounce((value: string) => {
      setDebouncedSearchValue(value);
      setCurrentPage(1);
    }, 100)
  );

  useEffect(() => {
    const debouncedFn = debouncedUpdateRef.current;
    return () => {
      debouncedFn.cancel();
    };
  }, []);

  const handleInputChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value;
      setSearchValue(value);
      debouncedUpdateRef.current(value);
    },
    []
  );

  const handleActiveViewChange = (view: "kategorie" | "zestawy") => {
    setActiveView(view);
  };

  useEffect(() => {
    setCurrentPage(1);
    setCurrentSortBy("views");
    setAscending(false);
  }, [activeView]);

  return {
    // Pagination
    currentPage,
    setCurrentPage,

    // Sorting
    currentSortBy,
    sortByForApi,
    ascending,
    handleSortChange,

    // Search
    searchValue,
    debouncedSearchValue,
    handleInputChange,

    //activeView
    activeView,
    handleActiveViewChange,
  };
};
