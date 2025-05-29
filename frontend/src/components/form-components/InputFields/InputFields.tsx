import { useId } from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFieldContext } from "@/config/form-context";
// import { TEInput, TERipple } from "tw-elements-react"; <- better to use custom components if needed

type FieldProps = React.InputHTMLAttributes<HTMLInputElement> & {
    label: string;
    className?: string;
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
        <div className={`flex flex-col gap-1`}>
            <label htmlFor={id}>
                {label}
            </label>
            <input
                id={id}
                className={`${className}`}
                name={field.name}
                value={field.state.value}
                type={type}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                {...rest}
            />
            <ErrorContainer errors={field.state.meta.errors} />
        </div>
    );
};

type Option = { label: string; value: string };

type SelectFieldProps = {
  label: string;
  options: Option[];
  className?: string;
};

export const SelectField = ({ label, options, className = "" }: SelectFieldProps) => {
  const id = useId();
  const field = useFieldContext<string>();

  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id}>{label}</label>
      <select
        id={id}
        name={field.name}
        value={field.state.value}
        onChange={(e) => field.handleChange(e.target.value)}
        onBlur={field.handleBlur}
        className={`${className} px-4 py-2 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 text-black bg-white`}
      >
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      <ErrorContainer errors={field.state.meta.errors} />
    </div>
  );
};
