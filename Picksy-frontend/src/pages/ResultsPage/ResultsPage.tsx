import { useParams } from "react-router-dom";
import { CategoryResults } from "../../components/CategoryResults/CategoryResults";
import { Loading } from "../../components/Loading/Loading";
import Navbar from "../../components/Navbar/Navbar";
import { useResults } from "../../hooks/useResults";
import useShowMore from "../../hooks/useShowMore";
import "./ResultsPage.css";

const ResultPage = () => {
  const roomCode: string = useParams().roomCode || "";
  const { error, categories, loading } = useResults({ roomCode });
  const { showMore, handleShowMore } = useShowMore();

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return (
      <>
        <Navbar />
        <div className="error-message">{error || "Wystapił błąd"}</div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="result-page">
        <div className="result-header-container">
          <div className="main-header">
            <div className="header-content">
              <h1 className="category-title">Podsumowanie</h1>
            </div>
          </div>
        </div>
        {categories.map((category) => (
          <CategoryResults
            key={category.id}
            category={category}
            handleShowMore={handleShowMore}
            showMore={showMore}
          />
        ))}
      </div>
    </>
  );
};

export default ResultPage;
