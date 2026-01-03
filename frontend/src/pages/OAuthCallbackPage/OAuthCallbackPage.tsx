import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../../context/userContext";
import { getUserFromCookies } from "../../api/authApi";

export default function OAuthCallbackPage() {
  const { setUser } = useUser();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      const response = await getUserFromCookies(); // pobiera dane użytkownika z JWT
      if (response?.status === 200) {
        setUser(response.result);
        navigate("/");
      } else {
        navigate("/login");
      }
    };

    fetchUser();
  }, []);

  return <p>Logowanie przez Google...</p>;
}
