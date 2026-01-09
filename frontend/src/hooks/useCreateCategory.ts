import { useState, type RefObject } from "react";
import { useNavigate } from "react-router-dom";
import {
  addOptionImage,
  createCategory,
  createOption,
  deleteOption,
  deleteOptionImage,
  setCategoryImage,
  updateCategory,
  updateOption,
} from "../api/categoryApi";
import type { CategoryOption } from "../components/CategoryOptions/CategoryOptions";
import { useUser } from "../context/userContext";
import type { CategoryType } from "../types/CategoryDetails";
import type { Option } from "../types/Option";

type useCreateCategoryReturn = {
  handleRemoveImage: () => void;
  handleFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleRemoveOption: (optionId: number) => void;
  handleOptionTitleChange: (optionId: number, newTitle: string) => void;
  handleOptionImageChange: (
    optionId: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => void;
  handleRemoveOptionImage: (optionId: number) => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
  handleAddOption: () => void;
  options: CategoryOption[];
  error: string;
  imagePreview: string | null;
  setImagePreview: (preview: string | null) => void;
};

const useCreateCategory = (
  selectedType: CategoryType,
  isPublic: boolean,
  title: string,
  description: string,
  fileInputRef: RefObject<HTMLInputElement | null>,
  editMode: boolean = false,
  categoryId?: number,
  onSave?: () => void,
  existedOptions?: Option[],
  setId?: number
): useCreateCategoryReturn => {
  const { user } = useUser();
  const [error, setError] = useState<string>("");
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [img, setImg] = useState<File | null>(null);

  const prepopulatedOptions =
    existedOptions?.map((opt) => ({
      id: opt.id,
      title: opt.name,
      image: null,
      imagePreview: opt.photoURL,
    })) || [];

  const [options, setOptions] = useState<CategoryOption[]>(prepopulatedOptions);
  const [originalOptions] = useState<CategoryOption[]>(prepopulatedOptions);

  const navigate = useNavigate();

  const saveCategory = async (): Promise<number> => {
    const response = await createCategory(
      title,
      selectedType,
      description,
      isPublic,
      setId
    );

    if (response.status !== 201) {
      setError("Wystapił problem podczas zapisywania kategorii.");
      return -1;
    } else {
      return response.result.id;
    }
  };

  const updateCategoryData = async (catId: number) => {
    const response = await updateCategory(
      catId,
      title,
      selectedType,
      description,
      isPublic
    );

    if (response.status !== 200) {
      setError("Wystąpił problem podczas aktualizacji kategorii.");
      return false;
    }
    return true;
  };

  const uploadCategoryImage = async (
    file: File,
    catId: number
  ): Promise<void> => {
    if (!user) return;
    console.log("Uploading category image for catId:", catId);
    const response = await setCategoryImage(catId, file);
    if (response.status !== 201) {
      setError("Wystąpił problem przy dodawaniu zdjęcia kategorii");
    } else {
      setError("");
    }
  };

  const saveOptions = async (catId: number): Promise<CategoryOption[]> => {
    try {
      const newOptions = await Promise.all(
        options.map(async (option) => {
          const response = await createOption(option.title, catId);
          if (response.status !== 201) {
            throw new Error(
              "Wystąpił problem podczas dodawania opcji dla kategorii."
            );
          }
          return { ...option, id: response.result.id };
        })
      );
      return newOptions;
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Błąd podczas zapisywania opcji");
      return [];
    }
  };

  const updateOptions = async (catId: number): Promise<void> => {
    try {
      // Get original option IDs
      const originalIds = originalOptions.map((opt) => opt.id);
      const currentIds = options
        .filter((opt) => opt.id > 0)
        .map((opt) => opt.id);

      // Delete removed options
      const toDelete = originalIds.filter((id) => !currentIds.includes(id));

      await Promise.all(toDelete.map((id) => deleteOption(id)));

      // Update or create options
      await Promise.all(
        options.map(async (option) => {
          if (option.id > 0) {
            // Update existing option
            const updateResponse = await updateOption(option.id, option.title);
            // Update image if changed
            let imageResponse = null;
            if (option.image) {
              imageResponse = await addOptionImage(option.image, option.id);
            }

            if (
              updateResponse.status !== 200 &&
              imageResponse &&
              imageResponse.status !== 200
            )
              throw new Error("Nie udało się zaktualizować opcji");
          } else {
            // Create new option
            const response = await createOption(option.title, catId);
            if (response.status !== 201) {
              throw new Error("Nie udało się utworzyć opcji");
            }
            const newOptionId = response.result.id;
            option.id = newOptionId;

            // Add image if provided
            if (option.image) {
              await addOptionImage(option.image, newOptionId);
            }
          }
        })
      );
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Błąd podczas aktualizacji opcji");
    }
  };

  const saveOptionsImage = async (
    optionsWithIds: CategoryOption[]
  ): Promise<void> => {
    await Promise.all(
      optionsWithIds.map(async (option) => {
        if (!option.image || !option.id) return;
        const response = await addOptionImage(option.image, option.id);
        if (response.status !== 201) {
          setError("Wystąpił problem podczas dodawania zdjęcia opcji");
        }
      })
    );
  };

  const handleRemoveImage = () => {
    setImagePreview(null);
    setImg(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    setImg(file ?? null);
    const reader = new FileReader();
    reader.onloadend = () => {
      setImagePreview(reader.result as string);
    };
    if (file) reader.readAsDataURL(file);
  };

  const handleAddOption = () => {
    const newOption: CategoryOption = {
      id: -(options.length + 1),
      title: "",
      image: null,
      imagePreview: null,
    };
    setOptions([...options, newOption]);
  };

  const handleRemoveOption = (optionId: number) => {
    setOptions(options.filter((opt) => opt.id !== optionId));
  };

  const handleOptionTitleChange = (optionId: number, newTitle: string) => {
    setOptions(
      options.map((opt) =>
        opt.id === optionId ? { ...opt, title: newTitle } : opt
      )
    );
  };

  const handleOptionImageChange = (
    optionId: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onloadend = () => {
      setOptions(
        options.map((opt) =>
          opt.id === optionId
            ? {
                ...opt,
                image: file,
                imagePreview: reader.result as string,
              }
            : opt
        )
      );
    };
    reader.readAsDataURL(file);
  };

  const handleRemoveOptionImage = async (optionId: number) => {
    setOptions(
      options.map((opt) =>
        opt.id === optionId ? { ...opt, image: null, imagePreview: null } : opt
      )
    );
    await deleteOptionImage(optionId);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate
    if (options.length < 2) {
      setError("Dodaj przynajmniej 2 opcje odpowiedzi");
      return;
    }

    const hasEmptyTitles = options.some((opt) => !opt.title.trim());
    if (hasEmptyTitles) {
      setError("Wszystkie opcje muszą mieć nazwę");
      return;
    }

    try {
      if (editMode && categoryId) {
        // Edit mode
        const success = await updateCategoryData(categoryId);
        if (!success) return;

        // Update category image if changed
        if (img) {
          await uploadCategoryImage(img, categoryId);
        }

        // Update options
        await updateOptions(categoryId);

        // Call onSave callback
        if (onSave) {
          onSave();
        }
      } else {
        // Create mode
        const newCategoryId = await saveCategory();
        if (newCategoryId < 0) return;

        // Upload category image
        if (img) await uploadCategoryImage(img, newCategoryId);

        // Create options and get their IDs
        const newOptions = await saveOptions(newCategoryId);

        // Upload option images
        await saveOptionsImage(newOptions);

        if (onSave) onSave();
        else navigate(`/category/${newCategoryId}`);
      }
    } catch (err: any) {
      console.error(err);
      setError("Wystąpił błąd podczas zapisywania");
    }
  };

  return {
    handleRemoveImage,
    handleFileChange,
    handleRemoveOption,
    handleOptionTitleChange,
    handleOptionImageChange,
    handleRemoveOptionImage,
    handleSubmit,
    handleAddOption,
    error,
    imagePreview,
    options,
    setImagePreview,
  };
};

export default useCreateCategory;
