import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./App.css";
import HomePage from "./pages/HomePage/HomePage";
import LoginPage from "./pages/AuthPages/LoginPage";
import SignUpPage from "./pages/AuthPages/SignUpPage";
import ForgotPasswordPage from "./pages/AuthPages/ForgotPasswordPage";
import ProfilePage from "./pages/ProfilePage/ProfilePage";
import { CategoryPage } from "./pages/CategoryPage/CategoryPage";
import VotingRoomSetup from "./pages/VotingRoomSetup/VotingRoomSetup";
import JoinRoom from "./pages/JoinRoomPage/JoinRoomPage";
import RoomPage from "./pages/RoomPage/RoomPage";
import CategoryDetailsPage from "./pages/CategoryDetailsPage/CategoryDetailsPage";
import CreateCategoryPage from "./pages/CreateCategoryPage/CreateCategoryPage";
import ResultsPage from "./pages/ResultsPage/ResultsPage";
import HistoryPage from "./pages/HistoryPage/HistoryPage";
import UsersPage from "./pages/UsersPage/UsersPage";
import OAuthCallbackPage from "./pages/OAuthCallbackPage/OAuthCallbackPage";
import UserProfilePage from "./pages/UserProfilePage/UserProfilePage";

const router = createBrowserRouter([
  { path: "/", element: <HomePage /> },
  { path: "/login", element: <LoginPage /> },
  { path: "/sign-up", element: <SignUpPage /> },
  { path: "/forgot-password", element: <ForgotPasswordPage /> },
  { path: "/profile", element: <ProfilePage /> },
  { path: "/profile/:userId", element: <UserProfilePage /> },
  { path: "/category", element: <CategoryPage /> },
  { path: "/:userId/category/", element: <CategoryPage /> },
  { path: "/category/:id", element: <CategoryDetailsPage /> },
  { path: "/room/create", element: <VotingRoomSetup /> },
  { path: "/room/join", element: <JoinRoom /> },
  { path: "/history", element: <HistoryPage /> },
  { path: "/room/:roomCode", element: <RoomPage /> },
  { path: "/room/:roomCode/results", element: <ResultsPage /> },
  { path: "/category/create", element: <CreateCategoryPage /> },
  { path: "/users", element: <UsersPage /> },
  { path: "/oauth2/callback", element: <OAuthCallbackPage /> },
]);

function App() {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (window as any).global ??= window;

  return <RouterProvider router={router} />;
}

export default App;
