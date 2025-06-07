import { useState } from "react";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { resetPasswordService } from "@/services/UserServices";
import { ResetPasswordRequestSchema } from "@/models/PasswordReset";
import { toast } from "react-hot-toast";

export const ResetPasswordScreen = () => {
  // Obtén el token directamente de la URL
  const token = new URLSearchParams(window.location.search).get("token") || "";
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      ResetPasswordRequestSchema.parse({ token, newPassword, confirmPassword });
      await resetPasswordService({ token, newPassword, confirmPassword });
      toast.success("Password reset successfully!");
    } catch (err: any) {
      toast.error(err.message || "Error resetting password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonLayout>
      <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
        <h1 className="text-2xl font-bold mb-4">Reset your password</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="password"
            placeholder="New password"
            value={newPassword}
            onChange={e => setNewPassword(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="password"
            placeholder="Confirm new password"
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
          <button
            type="submit"
            className="w-full bg-green-700 text-white py-2 rounded"
            disabled={loading}
          >
            {loading ? "Resetting..." : "Reset password"}
          </button>
        </form>
      </div>
    </CommonLayout>
  );
};