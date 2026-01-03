import { useState } from "react";
import "./AuthPages.css";
import { login } from "../../api/authApi";
import { useUser } from "../../context/userContext";
import { Link, useNavigate } from "react-router-dom";

function LoginPage() {
  const BACKEND_URL = "http://localhost:8080";
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [rememberMe, setRememberMe] = useState<boolean>(false);
  const { setUser } = useUser();
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState<string>("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const response = await login(email, password, rememberMe);
    if (response === undefined)
      setErrorMessage("Wystąpił błąd podczas logowania.");
    else {
      switch (response.status) {
        case 200:
          console.log("role - " + response.result.role);
          setUser({
            id: response.result.id,
            username: response.result.username,
            email: response.result.email,
            role: response.result.role,
            isBlocked: false,
          });
          navigate("/");
          break;
        case 400:
          setErrorMessage("Zły email lub hasło.");
          setEmail("");
          setPassword("");
          break;
        case 423:
          if (typeof response.error === "string")
            setErrorMessage(
              `Zostałeś zablokowany do: ${response.error.slice(
                response.error.indexOf(":") + 1,
                response.error.length
              )}`
            );
          else setErrorMessage("Zostałeś zablokowany");
          setEmail("");
          setPassword("");
          break;
        case 503:
          setErrorMessage("Serwis logowania nie dostępny.");
          break;
        default:
          setErrorMessage("Wystąpił błąd podczas logowania.");
          break;
      }
    }
  };

  const handleClick = () => {
    window.location.href = `${BACKEND_URL}/oauth2/authorization/google`;
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1 className="login-title">Witamy ponownie</h1>
          <p className="login-subtitle">Zaloguj się do swojego konta</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="twoj@email.pl"
              required
              className="form-input"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Hasło</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              className="form-input"
            />
          </div>

          <div className="form-options">
            <label className="remember-me">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                className="checkbox"
              />
              Zapamiętaj mnie
            </label>
            <Link to="/forgot-password" className="forgot-password">
              Zapomniałeś hasła?
            </Link>
          </div>

          <button type="submit" className="submit-button">
            Zaloguj się
          </button>

          {errorMessage !== "" && (
            <div className="error-message-auth">{errorMessage}</div>
          )}

          <div className="divider">
            <div className="divider-line"></div>
            <span className="divider-text">LUB</span>
            <div className="divider-line"></div>
          </div>

          <button type="button" className="google-button" onClick={handleClick}>
            <svg width="18" height="18" viewBox="0 0 18 18">
              <path
                fill="#4285F4"
                d="M16.51 8H8.98v3h4.3c-.18 1-.74 1.48-1.6 2.04v2.01h2.6a7.8 7.8 0 0 0 2.38-5.88c0-.57-.05-.66-.15-1.18z"
              />
              <path
                fill="#34A853"
                d="M8.98 17c2.16 0 3.97-.72 5.3-1.94l-2.6-2a4.8 4.8 0 0 1-7.18-2.54H1.83v2.07A8 8 0 0 0 8.98 17z"
              />
              <path
                fill="#FBBC05"
                d="M4.5 10.52a4.8 4.8 0 0 1 0-3.04V5.41H1.83a8 8 0 0 0 0 7.18z"
              />
              <path
                fill="#EA4335"
                d="M8.98 4.18c1.17 0 2.23.4 3.06 1.2l2.3-2.3A8 8 0 0 0 1.83 5.4L4.5 7.49a4.77 4.77 0 0 1 4.48-3.3z"
              />
            </svg>
            Kontynuuj z Google
          </button>

          <p className="signup-text">
            Nie masz konta?{" "}
            <Link to="/sign-up" className="signup-link">
              Zarejestruj się
            </Link>
          </p>
          <br />
          <br />
          <Link to="/" className="back-link">
            ← Powrót na stronę główną
          </Link>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
