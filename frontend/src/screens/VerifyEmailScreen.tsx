import { useEffect, useState } from "react";
import {verifyEmailService} from "@/services/UserServices.ts";
import {useLocation} from "wouter";

export const VerifyEmailScreen = () => {
    const [status, setStatus] = useState<"pending" | "success" | "error">("pending");
    const [, navigate] = useLocation();

    const queryParams = new URLSearchParams(window.location.search);
    const token = queryParams.get("token");
    console.log(token);

    useEffect(() => {
        const verify = async () => {
            try {
                if (!token) {
                    setStatus("error");
                    return;
                }

                await verifyEmailService(token);
                setStatus("success");
            } catch (err) {
                setStatus("error");
            }
        };
        verify();
    }, [token]);

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 px-4">
            <div className="bg-white p-8 rounded-xl shadow-md w-full max-w-md text-center" style={{color: "white"}}>
                <h1 className="text-2xl font-bold mb-4">Email Verification</h1>

                {status === "error" && (
                    <h3 className="text-2xl font-bold text-white">There was an error verifying your email. Please try again later.</h3>
                )}

                {status === "success" && (
                    <div className="flex flex-col items-center w-50">
                        <h3 className="text-2xl font-bold text-white mb-4">Your email has been verified successfully!</h3>
                        <img
                            src="/img/logo_3.webp"
                            alt="Logo"
                            className="mb-4"
                            style={{ minWidth: "4rem", minHeight: "4rem", maxWidth: "15rem", maxHeight: "15rem", display: "block", marginBottom: "4px"}}
                        />
                        <button
                            className="mb-4 p-3 rounded-md bg-red-600 text-white border border-red-700 text-sm font-medium"
                            onClick={() => navigate("/login")}
                        >
                            Login
                        </button>
                    </div>
                )}
                {status === "error" && (
                    <button
                        className="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                        onClick={() => navigate("/signup")}
                    >
                        Sign Up
                    </button>
                )}
            </div>
        </div>
    );
};
