import React from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFormContext } from "@/config/form-context";
import styles from "./FormContainer.module.css";

type Props = {
    extraError: Error | null;
    className?: string;
    children?: React.ReactNode;
    submitLabel?: string;
    submitDisabled?: boolean;
    hideSubmitButton?: boolean;
};

export const FormContainer = ({
    extraError, 
    className = "", 
    children, 
    submitLabel = "Submit",
    submitDisabled = false,
    hideSubmitButton = false,
}: Props) => {
    const form = useFormContext();

    return (
        <form
            className={`flex flex-col ${className}`}
            onSubmit={(e) => {
                form.handleSubmit();
                e.preventDefault();
                e.stopPropagation();
            }}
        >
            {children}
            {extraError && <ErrorContainer errors={[extraError]} />}
            {!hideSubmitButton && (
              <button
                type="submit"
                disabled={submitDisabled}
                className={`${styles.submitButton} w-full bg-green-700 hover:bg-green-800 text-white py-2 px-4 rounded transition`}
              >
                  {submitLabel}
              </button>
            )}
        </form>
    );
};
