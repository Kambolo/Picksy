import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar/Navbar";
import PageNavigation from "../../components/PageNavigation/PageNavigation";
import UsersList from "../../components/UserList/UserList";
import { usePaginationAndSort } from "../../hooks/usePaginationAndSort";
import { useUsersData } from "../../hooks/useUsersData";
import "./UsersPage.css";

const UsersPage = () => {
  const navigate = useNavigate();

  // Pagination, sorting and search
  const {
    currentPage,
    setCurrentPage,
    sortByForApi,
    ascending,
    handleSortChange,
    searchValue,
    handleInputChange,
  } = usePaginationAndSort();

  const { error, isLoading, totalPages, users } = useUsersData(
    currentPage,
    sortByForApi,
    ascending,
    searchValue
  );

  const handleUserClick = (userId: number) => {
    navigate(`/profile/${userId}`);
  };

  return (
    <div>
      <Navbar />
      <PageNavigation
        value={searchValue}
        handleSearch={handleInputChange}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        onSortChange={handleSortChange}
      />
      {error && (
        <div className="room-page-container">
          <div className="error-state">{error}</div>
        </div>
      )}

      <UsersList
        users={users}
        onUserClick={handleUserClick}
        isLoading={isLoading}
      />
    </div>
  );
};

export default UsersPage;
