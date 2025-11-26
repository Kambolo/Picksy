import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { UserProvider } from "./context/userContext.tsx";
import { CategoryUIProvider } from "./context/categoryContext.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <UserProvider>
      <CategoryUIProvider>
        <App />
      </CategoryUIProvider>
    </UserProvider>
  </StrictMode>
);
