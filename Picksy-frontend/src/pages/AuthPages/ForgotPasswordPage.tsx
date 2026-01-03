import { useState } from "react";
import "./AuthPages.css";
import { Link } from "react-router-dom";
import { resetPassword, sendCodeViaEmail, verifyCode } from "../../api/authApi";

function ForgotPasswordPage() {
  const [email, setEmail] = useState<string>("");
  const [code, setCode] = useState<string>("");
  const [newPassword, setNewPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [step, setStep] = useState<"email" | "code" | "password" | "success">(
    "email"
  );
  const [errorMessage, setErrorMessage] = useState<string>("");

  const handleSendCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    const response = await sendCodeViaEmail(email);
    if (response.status === 200) {
      setStep("code");
    } else {
      if (response.error !== null) {
        console.log("Error sending code:", response.error);
        setErrorMessage(response.error);
      }
    }
  };

  const handleVerifyCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    const response = await verifyCode(email, code);
    if (response.status === 200) {
      setStep("password");
    } else {
      if (response.error !== null) {
        setErrorMessage(response.error);
      }
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    if (newPassword !== confirmPassword) {
      setErrorMessage("Hasła nie są identyczne");
      return;
    }

    if (newPassword.length < 8) {
      setErrorMessage("Hasło musi mieć minimum 8 znaków");
      return;
    }

    const response = await resetPassword(email, code, newPassword);
    if (response.status === 200) {
      setStep("success");
    } else {
      if (response.error !== null) {
        setErrorMessage(response.error);
      }
    }
  };

  // Step 1: Enter email
  if (step === "email") {
    return (
      <div className="login-container">
        <div className="login-card">
          <div className="login-header">
            <h1 className="login-title">Zapomniałeś Hasła?</h1>
            <p className="login-subtitle">
              Podaj swój email, a wyślemy ci kod do zresetowania hasła
            </p>
          </div>

          {errorMessage && (
            <div className="error-message-auth">{errorMessage}</div>
          )}

          <form onSubmit={handleSendCode} className="login-form">
            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="twoj@email.com"
                required
                className="form-input"
              />
            </div>

            <button type="submit" className="submit-button">
              Wyślij kod
            </button>

            <Link to="/login" className="back-link">
              ← Powrót na strone Logowania
            </Link>
          </form>
        </div>
      </div>
    );
  }

  // Step 2: Verify code
  if (step === "code") {
    return (
      <div className="login-container">
        <div className="login-card">
          <div className="login-header">
            <h1 className="login-title">Wpisz Kod Weryfikacyjny</h1>
            <p className="login-subtitle">
              Wysłaliśmy kod na twój email: <strong>{email}</strong>
            </p>
          </div>

          {errorMessage && (
            <div className="error-message-auth">{errorMessage}</div>
          )}

          <form onSubmit={handleVerifyCode} className="login-form">
            <div className="form-group">
              <label className="form-label">Kod weryfikacyjny</label>
              <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="Wpisz kod z emaila"
                required
                className="form-input"
              />
            </div>

            <button type="submit" className="submit-button">
              Weryfikuj kod
            </button>

            <button
              type="button"
              onClick={() => setStep("email")}
              className="back-link"
            >
              ← Zmień email
            </button>
          </form>
        </div>
      </div>
    );
  }

  // Step 3: Reset password
  if (step === "password") {
    return (
      <div className="login-container">
        <div className="login-card">
          <div className="login-header">
            <h1 className="login-title">Ustaw Nowe Hasło</h1>
            <p className="login-subtitle">Wprowadź swoje nowe hasło</p>
          </div>

          {errorMessage && (
            <div className="error-message-auth">{errorMessage}</div>
          )}

          <form onSubmit={handleResetPassword} className="login-form">
            <div className="form-group">
              <label className="form-label">Nowe hasło</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Minimum 8 znaków"
                required
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Potwierdź hasło</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Wpisz hasło ponownie"
                required
                className="form-input"
              />
            </div>

            <button type="submit" className="submit-button">
              Zresetuj hasło
            </button>
          </form>
        </div>
      </div>
    );
  }

  // Step 4: Success
  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="success-icon">✓</div>
          <h1 className="login-title">Hasło Zostało Zmienione!</h1>
          <p className="login-subtitle">
            Możesz teraz zalogować się używając nowego hasła
          </p>
        </div>
        <Link to="/login" className="back-link">
          ← Przejdź do logowania
        </Link>
      </div>
    </div>
  );
}

export default ForgotPasswordPage;
