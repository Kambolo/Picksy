import { createContext, useContext, useEffect, useState } from "react";
import { getUserFromCookies } from "../api/authApi";
import type { User } from "../types/UserProfile";

type UserContextType = {
  user: User | null;
  setUser: React.Dispatch<React.SetStateAction<User | null>>;
};

const UserContext = createContext<UserContextType>({
  user: null,
  setUser: () => {},
});

export const UserProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const checkAndSetUser = async () => {
      const userFromApi = await getUserFromCookies();
      if (userFromApi.result) {
        setUser(userFromApi.result);
        localStorage.setItem("user", JSON.stringify(userFromApi));
      } else {
        setUser(null);
        localStorage.removeItem("user");
      }
    };

    checkAndSetUser();
  }, []);

  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useUser = () => useContext(UserContext);
