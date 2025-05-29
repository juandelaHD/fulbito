import React from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFormContext } from "@/config/form-context";

type Props = {
    extraError: Error | null;
    className?: string;
    children?: React.ReactNode;
    submitLabel?: string;    
};

export const FormContainer = ({
    extraError, 
    className = "", 
    children, 
    submitLabel = "Submit",
}: Props) => {
    const form = useFormContext();

    return (
        <form
            className={`flex flex-col ${className}`}
            onSubmit={(e) => {
                e.preventDefault();
                e.stopPropagation();
                form.handleSubmit();

            }}
        >
            {children}
            {extraError && <ErrorContainer errors={[extraError]} />}
        <button
            type="submit"
            className="mt-4 bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded"
        >
            {submitLabel}
        </button>            
        </form>
    );
};
