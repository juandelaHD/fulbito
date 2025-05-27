import { QueryClientProvider } from "@tanstack/react-query";
import { Navigation } from "@/Navigation";
import { appQueryClient } from "@/config/app-query-client";
import { TokenProvider } from "@/services/TokenContext";
import { Toaster } from "react-hot-toast";

export function App() {
  return (
    <>
      <Toaster position="top-center" />
      <QueryClientProvider client={appQueryClient}>
        <TokenProvider>
          <Navigation />
        </TokenProvider>
      </QueryClientProvider>
    </>
  );
}