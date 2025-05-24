import React from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { SubmitButton } from "@/components/form-components/SubmitButton/SubmitButton";
import { useFormContext } from "@/config/form-context";

type Props = {
  extraError: Error | null;
  className?: string;
  children?: React.ReactNode;
};

export const FormContainer = ({ extraError, className = "", children }: Props) => {
  const form = useFormContext();

  return (
    <form
      className={className}
      onSubmit={(e) => {
        e.preventDefault();
        e.stopPropagation();
        form.handleSubmit();
      }}
    >
      {children}
      {extraError && <ErrorContainer errors={[extraError]} />}
      <SubmitButton />
    </form>
  );
};