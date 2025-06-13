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
    const [tokenState, setTokenState] = useToken();


    const logOut = () => {
        setTokenState({ state: "LOGGED_OUT" });
    };

    if (tokenState.state !== "LOGGED_IN") return null;
    const { role } = tokenState;

    return (
        <>
            <Link className={styles.navLink} href="/">
                Main Page
            </Link>

            { role === "ADMIN" && (
                <>
                    <Link className={styles.navLink} href="/fields/new">
                        Create Field
                    </Link>
                    <Link className={styles.navLink} href="/fields/management">
                        Manage Field
                    </Link>
                </>

            )}

            { role === "USER" && (
                <>
                    <Link className={styles.navLink} href="/fields">
                        View Fields
                    </Link>
                    <Link className={styles.navLink} href="/match">
                        Matches
                    </Link>
                    <Link className={styles.navLink} href="/teams">
                        Teams
                    </Link>
                </>
            )}

            <button onClick={logOut} className={styles.navLink}>
                Log out
            </button>
        </>
    );
};