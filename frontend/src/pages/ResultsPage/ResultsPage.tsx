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
  console.log(categories);

  // Grupowanie kategorii po setId
  const groupedBySet = categories.reduce((acc, category) => {
    if (!category) return acc; // Skip null categories
    if (!acc[category.setId]) {
      acc[category.setId] = {
        setId: category.setId,
        setTitle: category.setTitle,
        categories: [],
      };
    }
    acc[category.setId].categories.push(category);
    return acc;
  }, {} as Record<number, { setId: number; setTitle: string; categories: typeof categories }>);

  const sets = Object.values(groupedBySet);

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
              <h1 className="main-title">Podsumowanie</h1>
            </div>
          </div>
        </div>

        {sets.map((set) => (
          <div key={set.setId} className="set-group-wrapper">
            <div className="set-group-container">
              <div className="set-header-result">
                <h2 className="set-title-result">{set.setTitle}</h2>
              </div>
              <div className="set-categories">
                {set.categories.map((category) => (
                  <CategoryResults
                    key={category.id}
                    category={category}
                    handleShowMore={handleShowMore}
                    showMore={showMore}
                  />
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </>
  );
};

export default ResultPage;
