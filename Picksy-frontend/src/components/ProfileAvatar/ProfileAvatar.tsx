import React, { type ReactElement } from "react";
import { FaEdit } from "react-icons/fa";
import "./ProfileAvatar.css";

type ProfileAvatarProps = {
  avatarUrl: string;
  username: string;
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  onAvatarClick: () => void;
  onFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

const ProfileAvatar = ({
  avatarUrl,
  username,
  fileInputRef,
  onAvatarClick,
  onFileChange,
}: ProfileAvatarProps): ReactElement => {
  return (
    <div className="profile-header">
      <div className="profile-avatar-wrapper">
        <img src={avatarUrl} alt={username} className="profile-avatar" />
        <button
          className="edit-avatar-btn"
          aria-label="Edit avatar"
          onClick={onAvatarClick}
        >
          <FaEdit size="1.2em" />
        </button>
        <input
          type="file"
          ref={fileInputRef}
          accept=".jpg,.jpeg,.png"
          style={{ display: "none" }}
          onChange={onFileChange}
        />
      </div>
    </div>
  );
};

export default ProfileAvatar;
