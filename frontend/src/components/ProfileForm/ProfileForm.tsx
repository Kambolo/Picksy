import React, { type ReactElement } from "react";
import { FaUser, FaFileAlt } from "react-icons/fa";
import "./ProfileForm.css";
import type { UserProfile } from "../../types/UserProfile";

type ProfileFormProps = {
  profile: UserProfile;
  editFields: boolean;
  editError: string | null;
  onEditProfile: (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => void;
  onSave: () => void;
  onEdit: () => void;
  onCancel: () => void;
};

const ProfileForm = ({
  profile,
  editFields,
  editError,
  onEditProfile,
  onSave,
  onEdit,
  onCancel,
}: ProfileFormProps): ReactElement => {
  return (
    <div className="profile-content">
      <div className="profile-section">
        <div className="section-header">
          <FaUser className="section-icon" size="1.2em" />
          <h2>Nazwa użytkownika</h2>
        </div>
        <input
          type="text"
          name="username"
          value={profile.username}
          onChange={onEditProfile}
          disabled={!editFields}
        />
      </div>

      <div className="profile-section">
        <div className="section-header">
          <FaFileAlt className="section-icon" size="1.2em" />
          <h2>Bio</h2>
        </div>
        <textarea
          name="bio"
          value={profile.bio}
          onChange={onEditProfile}
          disabled={!editFields}
        />
      </div>

      {editError && <div className="error-message">{editError}</div>}

      <div className="profile-actions">
        {editFields ? (
          <>
            <button className="btn btn-primary" onClick={onSave}>
              Zapisz zmiany
            </button>
            <button className="btn btn-secondary" onClick={onCancel}>
              Anuluj
            </button>
          </>
        ) : (
          <button className="btn btn-primary" onClick={onEdit}>
            Edytuj Profil
          </button>
        )}
      </div>
    </div>
  );
};

export default ProfileForm;
