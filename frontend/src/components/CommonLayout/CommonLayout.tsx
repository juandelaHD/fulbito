import React from "react";
import { Link, useLocation } from "wouter";
import { useToken } from "@/services/TokenContext";
import styles from "./CommonLayout.module.css";

export const CommonLayout = ({ children }: React.PropsWithChildren) => {
    const [tokenState] = useToken();
    const [location] = useLocation();

    const hideNav = location === "/login" || location === "/signup";

    return (
        <div className={styles.mainLayout}>
            {!hideNav && (
                <nav className={styles.navbar}>
                    <div className={styles.navLinks}>
                        {tokenState.state === "LOGGED_OUT" ? <LoggedOutLinks /> : <LoggedInLinks />}
                    </div>
                </nav>
            )}
            <div className={styles.body}>{children}</div>
        </div>
    );
};

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

const LoggedInLinks = () => {
    const [, setTokenState] = useToken();

    const logOut = () => {
        setTokenState({ state: "LOGGED_OUT" });
    };

    return (
        <>
            <Link className={styles.navLink} href="/under-construction">
                Main Page
            </Link>
            <Link className={styles.navLink} href="/fields/new">
                Create Field
            </Link>
            <Link className={styles.navLink} href="/fields">
                View Fields
            </Link>
            <button onClick={logOut} className={styles.navLink}>
                Log out
            </button>
        </>
    );
};