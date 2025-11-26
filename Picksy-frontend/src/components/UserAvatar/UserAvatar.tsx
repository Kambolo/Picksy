import { User } from "lucide-react";
import { useState } from "react";

type UserAvatarProps = {
  avatarUrl: string;
  username: string;
};

const UserAvatar: React.FC<UserAvatarProps> = ({ avatarUrl, username }) => {
  const [imageError, setImageError] = useState(false);

  if (!avatarUrl || imageError) {
    return (
      <div className="avatar-placeholder">
        <User size={48} />
      </div>
    );
  }

  return (
    <img
      src={avatarUrl}
      alt={`${username}'s avatar`}
      className="user-avatar"
      onError={() => setImageError(true)}
    />
  );
};

export default UserAvatar;
