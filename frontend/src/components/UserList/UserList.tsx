import { User } from "lucide-react";
import type { UserProfile } from "../../types/UserProfile";
import UserCard from "../UserCard/UserCard";

type UserListProps = {
  users: UserProfile[];
  onUserClick: (id: number) => void;
  isLoading: boolean;
};

const UsersList: React.FC<UserListProps> = ({
  users,
  onUserClick,
  isLoading,
}) => {
  if (isLoading) {
    return (
      <div className="users-list">
        {[1, 2, 3, 4, 5].map((i) => (
          <UserCard
            key={i}
            user={{
              id: -1,
              username: "",
              email: "",
              avatarUrl: "",
              bio: "",
              isBlocked: false,
              role: "USER",
            }}
            onClick={() => {}}
            isLoading={true}
          />
        ))}
      </div>
    );
  }

  if (users.length === 0) {
    return (
      <div className="empty-state-user">
        <User size={48} />
        <p>Nie znaleziono użytkowników</p>
      </div>
    );
  }

  return (
    <div className="users-list">
      {users.map((user) => (
        <UserCard
          key={user.id}
          user={user}
          onClick={onUserClick}
          isLoading={isLoading}
        />
      ))}
    </div>
  );
};

export default UsersList;
