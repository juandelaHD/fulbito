import React from "react";
import { Link, useLocation } from "wouter";
import { useToken } from "@/services/TokenContext";
import styles from "./CommonLayout.module.css";

export const CommonLayout = ({ children }: React.PropsWithChildren) => {
  const [tokenState, setTokenState] = useToken();
  const [location, navigate] = useLocation();

  const hideNav = location === "/login" || location === "/signup";

  const logOut = () => {
    setTokenState({ state: "LOGGED_OUT" });
    navigate("/login");
  };

  return (
    <div className={styles.mainLayout}>
      {!hideNav && (
        <nav className={styles.navbar}>
          <div className={styles.navLinks}>
            {tokenState.state === "LOGGED_OUT" ? (
              <div className={styles.navLinksRight}>
                <LoggedOutLinks />
              </div>
            ) : (
              <>
                <div className={styles.navLinksLeft}>
                  <Link className={styles.navLink} href="/">Main Page</Link>
                  <Link className={styles.navLink} href="/fields/new">Create Field</Link>
                  <Link className={styles.navLink} href="/fields/management">Manage Field</Link>
                  <Link className={styles.navLink} href="/fields">View Fields</Link>
                  <Link className={styles.navLink} href="/match">Matches</Link>
                  <Link className={styles.navLink} href="/search">Search Users</Link>
                </div>
                <div className={styles.navLinksRight}>
                  <Link className={styles.navLink} href="/profile">My Profile</Link>
                  <button onClick={logOut} className={styles.navLink}>
                    Log out
                  </button>
                </div>
              </>
            )}
          </div>
        </nav>
      )}
      <div className={styles.body}>{children}</div>
    </div>
  );
}

const LoggedOutLinks = () => (
    <>
        <Link className={styles.navLink} href="/login">
            Log in
        </Link>
        <Link className={styles.navLink} href="/signup">
            Sign Up
        </Link>
    </>
);