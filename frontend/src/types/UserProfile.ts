export type UserProfile = User & Omit<Profile, "userId">;

export type Profile = {
  userId: number;
  avatarUrl: string;
  bio: string;
};

export type User = {
  id: number;
  username: string;
  email: string;
  role: "USER" | "ADMIN";
  isBlocked: boolean;
};
