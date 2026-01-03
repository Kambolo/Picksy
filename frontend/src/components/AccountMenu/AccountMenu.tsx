import { useEffect, useRef, useState, type ReactElement } from "react";
import type { IconType } from "react-icons";
import { FaSignOutAlt, FaUser } from "react-icons/fa";
import { FaUserCheck } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../../api/authApi";
import { useUser } from "../../context/userContext";
import "./AccountMenu.css";

interface DropdownItem {
  label: string;
  icon: IconType;
}

const AccountMenu = (): ReactElement => {
  const { user, setUser } = useUser();
  const [isOpen, setOpen] = useState<boolean>(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const dropdownContent: DropdownItem[] = [
    { label: "Profil", icon: FaUser },
    { label: "Wyloguj", icon: FaSignOutAlt },
  ];

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent): void => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleMenuItemClick = async (label: string): Promise<void> => {
    switch (label) {
      case "Profil":
        navigate("/profile");
        break;
      case "Wyloguj":
        await logout();
        localStorage.removeItem("categories");
        setUser(null);
        break;
    }
    setOpen(false);
  };

  return (
    <div ref={menuRef} className="account-menu-container">
      {user ? (
        <button
          className="account-button"
          onClick={() => setOpen(!isOpen)}
          aria-label="Account menu"
        >
          <div className="sign-in-button">
            <FaUserCheck size="2.3rem" />
          </div>
        </button>
      ) : (
        <Link
          to="/login"
          className="account-button sign-in-button"
          aria-label="Login"
        >
          <FaUser size="2rem" />
        </Link>
      )}

      {isOpen && (
        <div className="drop-down-content">
          <ul>
            {dropdownContent.map((item: DropdownItem) => {
              const Icon = item.icon;
              return (
                <li key={item.label}>
                  <button onClick={() => handleMenuItemClick(item.label)}>
                    <Icon />
                    {item.label}
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
};

export default AccountMenu;
