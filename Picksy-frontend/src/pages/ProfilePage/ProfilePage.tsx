import { useEffect, type ReactElement } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../../context/userContext";
import { useProfileData } from "../../hooks/useProfileData";
import { useProfileEdit } from "../../hooks/useProfileEdit";
import { useImageUpload } from "../../hooks/useImageUpload";
import Navbar from "../../components/Navbar/Navbar";
import ProfileAvatar from "../../components/ProfileAvatar/ProfileAvatar";
import ProfileForm from "../../components/ProfileForm/ProfileForm";
import "./ProfilePage.css";
import { ImSpinner } from "react-icons/im";

const ProfilePage = (): ReactElement => {
  const { user } = useUser();
  const navigate = useNavigate();

  // Redirect if no user
  useEffect(() => {
    if (!user) navigate("/");
  }, [user, navigate]);

  // Profile data management
  const {
    profile,
    setProfile,
    isLoading,
    setIsLoading,
    error,
    handleAvatarUpload,
  } = useProfileData(user?.id ?? null);

  // Profile editing logic
  const {
    editFields,
    editError,
    handleEditProfile,
    handleEditClick,
    handleCancelEdit,
    handleSave,
  } = useProfileEdit(profile, setProfile, setIsLoading);

  // Avatar upload logic
  const { fileInputRef, handleFileInputClick, handleFileInputChange } =
    useImageUpload(handleAvatarUpload);

  // Loading state
  if (isLoading) {
    return (
      <div className="profile-container">
        <div className="loading-spinner">
          <ImSpinner size={32} />
        </div>
      </div>
    );
  }

  // Error state
  if (error || !profile) {
    return (
      <>
        <Navbar />
        <div className="profile-container">
          <div className="error-message">{error || "Profile not found"}</div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="profile-container">
        <div className="profile-card">
          <ProfileAvatar
            avatarUrl={profile.avatarUrl}
            username={profile.username}
            fileInputRef={fileInputRef}
            onAvatarClick={handleFileInputClick}
            onFileChange={handleFileInputChange}
          />
          <ProfileForm
            profile={profile}
            editFields={editFields}
            editError={editError}
            onEditProfile={handleEditProfile}
            onSave={handleSave}
            onEdit={handleEditClick}
            onCancel={handleCancelEdit}
          />
        </div>
      </div>
    </>
  );
};

export default ProfilePage;
