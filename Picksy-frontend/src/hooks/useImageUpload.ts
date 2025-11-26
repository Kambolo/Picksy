import { useRef } from "react";

export const useImageUpload = (onUpload: (file: File) => Promise<void>) => {
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  // Open file input dialog
  const handleFileInputClick = (): void => {
    fileInputRef.current?.click();
  };

  // Handle file selection
  const handleFileInputChange = async (
    event: React.ChangeEvent<HTMLInputElement>
  ): Promise<void> => {
    const file = event.target.files?.[0];

    if (!file) return;

    try {
      await onUpload(file);
    } finally {
      // Reset input to allow uploading the same file again
      event.target.value = "";
    }
  };

  return {
    fileInputRef,
    handleFileInputClick,
    handleFileInputChange,
  };
};
