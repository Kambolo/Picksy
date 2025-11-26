import { useState } from "react";
import "./AuthPages.css";
import { Link, useNavigate } from "react-router-dom";
import { signup } from "../../api/authApi";

interface PasswordValidation {
  length: boolean;
  uppercase: boolean;
  special: boolean;
}

function SignUpPage() {
  const BACKEND_URL = "http://localhost:8080";
  const [errorMessage, setErrorMessage] = useState<string>("");
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [acceptTerms, setAcceptTerms] = useState<boolean>(false);
  const [passwordValidation, setPasswordValidation] =
    useState<PasswordValidation>({
      length: false,
      uppercase: false,
      special: false,
    });

  const validatePassword = (password: string): PasswordValidation => {
    return {
      length: password.length >= 8 && password.length <= 20,
      uppercase: /[A-Z]/.test(password),
      special: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    };
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });

    if (name === "password") {
      setPasswordValidation(validatePassword(value));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !passwordValidation.length ||
      !passwordValidation.uppercase ||
      !passwordValidation.special
    ) {
      alert("Hasło nie spełnia wszystkich wymagań!");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      alert("Hasła nie są zgodne!");
      return;
    }

    if (!acceptTerms) {
      alert("Musisz zaakceptować regulamin!");
      return;
    }

    const response = await signup(formData);
    if (response === undefined)
      setErrorMessage("Wystąpił błąd podczas logowania.");
    else {
      if (response.status === 200) navigate("/login");
      else {
        setErrorMessage(response.error);
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
          <h1 className="login-title">Utwórz konto</h1>
          <p className="login-subtitle">Dołącz do nas już dziś</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {/* Name Input */}
          <div className="form-group">
            <label className="form-label">Nazwa uzytkownika</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="User1"
              required
              className="form-input"
            />
          </div>

          {/* Email Input */}
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="twoj@email.pl"
              required
              className="form-input"
            />
          </div>

          {/* Password Input */}
          <div className="form-group">
            <label className="form-label">Hasło</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
              className="form-input"
            />
            <div className="password-requirements">
              <div
                className={`requirement ${
                  passwordValidation.length ? "valid" : ""
                }`}
              >
                <span className="requirement-icon">
                  {passwordValidation.length ? "✓" : "○"}
                </span>
                <span>8-20 znaków</span>
              </div>
              <div
                className={`requirement ${
                  passwordValidation.uppercase ? "valid" : ""
                }`}
              >
                <span className="requirement-icon">
                  {passwordValidation.uppercase ? "✓" : "○"}
                </span>
                <span>Jedna wielka litera</span>
              </div>
              <div
                className={`requirement ${
                  passwordValidation.special ? "valid" : ""
                }`}
              >
                <span className="requirement-icon">
                  {passwordValidation.special ? "✓" : "○"}
                </span>
                <span>Znak specjalny (!@#$%...)</span>
              </div>
            </div>
          </div>

          {/* Confirm Password Input */}
          <div className="form-group">
            <label className="form-label">Potwierdź hasło</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="••••••••"
              required
              className="form-input"
            />
          </div>

          {/* Terms Checkbox */}
          <label className="terms-label">
            <input
              type="checkbox"
              checked={acceptTerms}
              onChange={(e) => setAcceptTerms(e.target.checked)}
              className="checkbox"
              required
            />
            <span>
              Akceptuję{" "}
              <a
                href="https://app.websitepolicies.com/policies/view/xip0xg92"
                className="terms-link"
                target="_blank"
              >
                regulamin
              </a>{" "}
              i{" "}
              <a
                href="https://app.websitepolicies.com/policies/view/xip0xg92"
                className="terms-link"
                target="_blank"
              >
                politykę prywatności
              </a>
            </span>
          </label>

          {/* Submit Button */}
          <button type="submit" className="submit-button">
            Zarejestruj się
          </button>

          {errorMessage !== "" && (
            <div className="error-message-auth">{errorMessage}</div>
          )}

          {/* Divider */}
          <div className="divider">
            <div className="divider-line"></div>
            <span className="divider-text">LUB</span>
            <div className="divider-line"></div>
          </div>

          {/* Google Button */}
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
            Zarejestruj się przez Google
          </button>

          {/* Login Link */}
          <p className="signup-text">
            Masz już konto?{" "}
            <Link to="/login" className="signup-link">
              Zaloguj się
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default SignUpPage;
