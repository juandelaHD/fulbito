import { useEffect } from "react";

type Props = {
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
};

export function ConfirmDialog({ message, onConfirm, onCancel }: Props) {
    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === "Escape") onCancel();
        };
        window.addEventListener("keydown", handleEsc);
        return () => window.removeEventListener("keydown", handleEsc);
    }, [onCancel]);

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex justify-center items-center z-50">
            <div className="bg-white rounded-xl shadow-2xl p-6 w-full max-w-sm animate-fade-in space-y-4">
                <h2 className="text-lg font-semibold text-gray-800">Are you sure?</h2>
                <p className="text-sm text-gray-600">{message}</p>
                <div className="flex justify-end space-x-3 pt-2">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 text-sm rounded-md bg-gray-100 text-gray-800 hover:bg-gray-200 transition"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 text-sm rounded-md bg-red-600 text-white hover:bg-red-700 transition"
                    >
                        Delete
                    </button>
                </div>
            </div>
        </div>
    );
}
