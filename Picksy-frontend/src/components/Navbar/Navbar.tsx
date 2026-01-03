import Logo from "../Logo/Logo";
import DropdownMenu from "../DropdownMenu/DropdownMenu";
import "./Navbar.css";
import useWindowWidth from "../../hooks/useWindowWidth";
import { useEffect, useState } from "react";
import { IoMenu } from "react-icons/io5";
import AccountMenu from "../AccountMenu/AccountMenu";
import { useUser } from "../../context/userContext";
import { logout } from "../../api/authApi";

function Navbar() {
  const width: number = useWindowWidth();
  const isMobile: boolean = width < 670;
  const [showShortMenu, setShowShortMenu] = useState<boolean>(false);
  const { user, setUser } = useUser();

  const discoverDropdownItems = [{ title: "Odkryj", to: "/" }];

  const [categoriesDropdownItems, setCategoriesDropdownItems] = useState([
    { title: "Społeczność", to: "/category" },
  ]);

  const [votingDropdownItems, setVotingDropdownItems] = useState([
    { title: "Dołącz", to: "/room/join" },
  ]);

  useEffect(() => {
    if (user) {
      setCategoriesDropdownItems((prev) => {
        if (prev.length > 1) return prev;
        return [
          ...prev,
          { title: "Własne", to: `/${user.id}/category` },
          { title: "Stwórz kategorię", to: "/category/create" },
          { title: "Stwórz zestaw", to: "/set/create" },
        ];
      });
      setVotingDropdownItems((prev) => {
        if (prev.length > 1) return prev;
        return [
          ...prev,
          { title: "Stwórz", to: "/room/create" },
          { title: "Historia", to: "/history" },
        ];
      });
    }
  }, [user]);

  const usersDropdownItems = [{ title: "Użytkownicy", to: "/users" }];

  const guestUser = [{ title: "Zaloguj się", to: "/login" }];

  const showMenuOnClick = () => {
    setShowShortMenu(!showShortMenu);
  };

  const handleLogout = async () => {
    await logout();
    setUser(null);
  };

  const loggedUser = [
    { title: "Profil", to: "/profile" },
    { title: "Wyloguj się", onClick: handleLogout },
  ];

  return (
    <div className="navbar-container">
      <div className="navbar-core-container">
        <Logo />
        {!isMobile && (
          <>
            <div className="navbar-menu-group">
              <DropdownMenu title="Odkryj" items={discoverDropdownItems} />
              <DropdownMenu title="Kategorie" items={categoriesDropdownItems} />
              <DropdownMenu title="Głosowanie" items={votingDropdownItems} />
              <DropdownMenu title="Użytkownicy" items={usersDropdownItems} />
            </div>
            <AccountMenu />
          </>
        )}
        {isMobile && (
          <button className="short-menu-btn" onClick={() => showMenuOnClick()}>
            <IoMenu size={35} />
          </button>
        )}
      </div>
      {isMobile && (
        <div className={`short-menu-dropdown ${showShortMenu ? "open" : ""}`}>
          <ul className="short-menu-dropdown-content">
            <li>
              <DropdownMenu title="Odkryj" items={discoverDropdownItems} />
            </li>
            <li>
              <DropdownMenu title="Kategorie" items={categoriesDropdownItems} />
            </li>
            <li>
              <DropdownMenu title="Głosowanie" items={votingDropdownItems} />
            </li>
            <li>
              <DropdownMenu title="Użytkownicy" items={usersDropdownItems} />
            </li>
            {user ? (
              <li>
                <DropdownMenu title="Konto" items={loggedUser} />
              </li>
            ) : (
              <li>
                <DropdownMenu title="Konto" items={guestUser} />
              </li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
}

export default Navbar;
