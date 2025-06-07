import { useState } from "react";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { forgotPasswordService } from "@/services/UserServices";
import { ForgotPasswordRequestSchema } from "@/models/PasswordReset";
import { toast } from "react-hot-toast";

export const ForgotPasswordScreen = () => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Validación con zod
      ForgotPasswordRequestSchema.parse({ email });
      await forgotPasswordService({ email });
      toast.success("If the email exists, you will receive a link to reset your password.");
    } catch (err: any) {
      toast.error(err.message || "Error sending the email.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonLayout>
      <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
        <h1 className="text-2xl font-bold mb-4">Forgot your password?</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
          <button
            type="submit"
            className="w-full bg-green-700 text-white py-2 rounded"
            disabled={loading}
          >
            {loading ? "Sending..." : "Send link"}
          </button>
        </form>
      </div>
    </CommonLayout>
  );
};