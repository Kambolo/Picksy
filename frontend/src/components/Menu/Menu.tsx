import { Link } from "react-router-dom";
import "./Menu.css";

type MenuProp = {
  title: string;
  to: string;
};

export type MenuProps = {
  items: MenuProp[];
};

const Menu: React.FC<MenuProps> = ({ items }) => {
  return (
    <menu className="menu-container">
      <ul>
        {items.map((item, idx) => (
          <li key={idx}>
            <Link to={item.to}>{item.title}</Link>
          </li>
        ))}
      </ul>
    </menu>
  );
};

export default Menu;
