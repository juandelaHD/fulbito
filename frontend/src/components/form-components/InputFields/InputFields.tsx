import { useId } from "react";
import { ErrorContainer } from "@/components/form-components/ErrorContainer/ErrorContainer";
import { useFieldContext } from "@/config/form-context";

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
                           className,
                           ...rest
                       }: FieldProps & { type: string }) => {
    const id = useId();
    const field = useFieldContext<string>();

    return (
        <div className={`flex flex-col gap-1 ${className}`}>
            <label htmlFor={id}>
                {label}
            </label>
            <input
                id={id}
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
