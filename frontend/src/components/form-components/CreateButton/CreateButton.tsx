import { useFormContext } from "@/config/form-context";

type CreateButtonProps = {
  children?: React.ReactNode;
  className?: string;
};

export const CreateButton = ({ children = "Create", className = "" }: CreateButtonProps) => {
  const form = useFormContext();

  return (
    <form.Subscribe selector={(state) => [state.canSubmit, state.isSubmitting]}>
      {([canSubmit, isSubmitting]) => (
        <button
          type="submit"
          disabled={!canSubmit}
          className={`w-full bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded-xl shadow-md transition duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed ${className}`}
        >
          {isSubmitting ? "..." : children}
        </button>
      )}
    </form.Subscribe>
  );
};
