import { useEffect, useState } from "react";
import type { CategoryDetails } from "../types/CategoryDetails";
import { getCategory, getCategoryOptions } from "../api/categoryApi";
import { getUser } from "../api/authApi";
import type { Option } from "../types/Option";

const useCategoryDetailsData = (id: number) => {
  const [category, setCategory] = useState<CategoryDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchCategoryData = async () => {
    try {
      if (!id) return;
      setLoading(true);
      const responseCategory = await getCategory(id);
      if (responseCategory.status !== 200) {
        throw new Error(
          "Wystapił problem podczas pobierania danych o kategorii"
        );
      }

      let author = "Picksy";
      if (responseCategory.result.authorID) {
        const responseUser = await getUser(responseCategory.result.authorID);
        if (responseUser.status !== 200) {
          throw new Error(
            "Wystapił problem podczas pobierania danych o autorze"
          );
        }
        author = responseUser.result.username;
      }

      const fetchedCategory: CategoryDetails = {
        id: responseCategory.result.id,
        name: responseCategory.result.name,
        type: responseCategory.result.type,
        author: author,
        authorID: responseCategory.result.authorID,
        description: responseCategory.result.description,
        photoURL: responseCategory.result.photoURL,
        options: [],
        views: responseCategory.result.views,
        created: responseCategory.result.created,
        isPublic: responseCategory.result.isPublic,
      };

      const responseOption = await getCategoryOptions(fetchedCategory.id);

      let options: Option[] = [];
      if (responseOption.status === 200) {
        options = responseOption.result;
        options.forEach((opt) => {
          if (!opt.photoURL) {
            opt.photoURL =
              "https://res.cloudinary.com/dctiucda1/image/upload/v1764698183/image_zaopfn.png";
          }
        });
      }

      const categoryData: CategoryDetails = { ...fetchedCategory, options };

      setCategory(categoryData);
      setLoading(false);
    } catch (er: any) {
      setLoading(false);
      setError(er?.message);
    }
  };

  useEffect(() => {
    fetchCategoryData();
  }, [id]);

  return {
    category,
    loading,
    setLoading,
    fetchCategoryData,
    error,
  };
};

export default useCategoryDetailsData;
