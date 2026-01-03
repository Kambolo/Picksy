import { Link } from "react-router-dom";
import "./Logo.css";

function Logo() {
  return (
    <div className="logo-text">
      <Link to="/"> Picksy </Link>
    </div>
  );
}

export default Logo;
