import { useCallback } from "react";
import { useBlocker } from "react-router-dom";

export function useNavigationBlocker(
  shouldBlock: () => boolean,
  dependencies: any[],
  onLeave: () => void
) {
  const blocker = useBlocker(useCallback(shouldBlock, dependencies));

  const confirmNavigation = useCallback(
    (useOnLeave: boolean) => {
      if (useOnLeave) onLeave();
      blocker.proceed?.(); // allows the navigation
    },
    [blocker, onLeave]
  );

  const cancelNavigation = useCallback(() => {
    blocker.reset?.(); // stops navigation and keeps user on current page
  }, [blocker]);

  return {
    isBlocked: blocker.state === "blocked",
    confirmNavigation,
    cancelNavigation,
  };
}
