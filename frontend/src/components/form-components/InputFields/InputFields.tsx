import { useId } from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFieldContext } from "@/config/form-context";
import styles from "./InputFields.module.css";

type FieldProps = React.InputHTMLAttributes<HTMLInputElement> & {
  label: string;
};

export const TextField = (props: FieldProps) => {
  return <FieldWithType {...props} type="text" />;
};

export const PasswordField = (props: FieldProps) => {
  return <FieldWithType {...props} type="password" />;
};

const FieldWithType = ({
  label,
  type,
  className = "",
  ...rest
}: FieldProps & { type: string }) => {
  const id = useId();
  const field = useFieldContext<string>();

  return (
    <>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <div className={styles.dataContainer}>
        <input
          id={id}
          name={field.name}
          value={field.state.value}
          className={`${styles.input} ${className}`}
          type={type}
          onBlur={field.handleBlur}
          onChange={(e) => field.handleChange(e.target.value)}
          {...rest}
        />
        <ErrorContainer errors={field.state.meta.errors} />
      </div>
    </>
  );
};
