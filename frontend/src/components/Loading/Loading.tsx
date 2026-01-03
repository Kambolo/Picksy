import Navbar from "../../components/Navbar/Navbar";
import { ImSpinner } from "react-icons/im";

export const Loading = () => (
  <div>
    <Navbar />
    <div className="loading-spinner">
      <ImSpinner />
    </div>
  </div>
);
