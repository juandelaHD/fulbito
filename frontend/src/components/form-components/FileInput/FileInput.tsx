import React from "react";
import styles from "./FileInput.module.css";

interface FileInputProps {
  label?: string;
  accept?: string;
  onChange: (file: File | null) => void;
}

export const FileInput: React.FC<FileInputProps> = ({
  label = "Select file",
  accept = "*/*",
  onChange,
}) => {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    onChange(files?.[0] ?? null); // ✅ garantiza File | null
  };

  return (
    <div className={styles.wrapper}>
      <label className={styles.label}>{label}</label>
      <input
        type="file"
        accept={accept}
        onChange={handleChange}
        className={styles.input}
      />
    </div>
  );
};
