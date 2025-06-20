import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import { App } from "@/App";
import type {} from "@/WindowEnv";

import Modal from "react-modal"

import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);

Modal.setAppElement("#root") 