import { useState } from "react";

type useShowMoreReturn = {
  showMore: boolean;
  handleShowMore: () => void;
};

const useShowMore = (): useShowMoreReturn => {
  const [showMore, setShowMore] = useState<boolean>(false);

  const handleShowMore = () => {
    setShowMore((prev) => !prev);
    const timeout = setTimeout(() => {}, 20);
    timeout;
  };

  return {
    showMore,
    handleShowMore,
  };
};

export default useShowMore;
