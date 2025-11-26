import { useEffect, useState } from "react";
import { getUsers } from "../api/authApi";
import type { UserProfile } from "../types/UserProfile";
import type { User } from "../types/User";
import { getProfiles } from "../api/profileApi";

const PAGE_SIZE = 10;

export const useUsersData = (
  currentPage: number,
  sortByForApi: string,
  ascending: boolean,
  searchValue: string
) => {
  const [totalPages] = useState(1);
  const [totalUsers] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const [users, setUsers] = useState<UserProfile[]>([]);

  //make request to account service
  const fetchUsers = async () => {
    setIsLoading(true);

    const userResponse = await getUsers(
      currentPage,
      PAGE_SIZE,
      ascending,
      searchValue
    );

    if (userResponse.status !== 200) {
      setIsLoading(false);
      setError("Wystapił błąd podczas pobierania użytkowników");
      return;
    }

    const usersFromApi: User[] = userResponse.result.content;

    if (usersFromApi.length === 0) {
      setIsLoading(false);
      setUsers([]);
      setError("");
      return;
    }

    const usersIds: number[] = usersFromApi.map((user) => user.id);

    const profileResponse = await getProfiles(usersIds);

    console.log(profileResponse);

    if (profileResponse.status !== 200) {
      setIsLoading(false);
      setError("Wystapił błąd podczas pobierania użytkowników");
      return;
    }

    const userProfile: UserProfile[] = usersFromApi.map((user) => {
      const profile = profileResponse.result.find(
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        (profile: any) => profile.userId === user.id
      );
      return { ...user, avatarUrl: profile.avatarUrl, bio: profile.bio };
    });

    console.log("users - " + usersFromApi);

    setUsers(userProfile);

    setIsLoading(false);
    setError("");
  };

  useEffect(() => {
    fetchUsers();
  }, [currentPage, sortByForApi, ascending, searchValue]);

  return {
    error,
    isLoading,
    totalPages,
    totalUsers,
    users,
  };
};
