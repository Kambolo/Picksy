import { FileText, Mail } from "lucide-react";
import type { UserProfile } from "../../types/UserProfile";
import UserAvatar from "../UserAvatar/UserAvatar";

type UserCardProps = {
  user: UserProfile;
  onClick: (id: number) => void;
  isLoading: boolean;
};

const UserCard: React.FC<UserCardProps> = ({ user, onClick, isLoading }) => {
  if (isLoading) {
    return (
      <div className="user-card-container skeleton">
        <div className="user-image-container">
          <div className="skeleton-avatar" />
        </div>
        <div className="user-card-center">
          <div className="skeleton-username" />
          <div className="user-info-row">
            <div className="skeleton-info" />
          </div>
          <div className="user-info-row">
            <div className="skeleton-bio" />
            <div className="skeleton-bio short" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="user-card-container" onClick={() => onClick(user.id)}>
      <div className="user-image-container">
        <UserAvatar avatarUrl={user.avatarUrl} username={user.username} />
      </div>

      <div className="user-card-center">
        <h2 className="user-card-username">{user.username}</h2>

        <div className="user-info-row">
          <Mail size={16} className="info-icon" />
          <p className="user-info-text">{user.email}</p>
        </div>

        {user.bio && (
          <div className="user-info-row">
            <FileText size={16} className="info-icon" />
            <p className="user-bio-text">{user.bio}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserCard;
