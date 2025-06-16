import React from "react";
import { Link, useLocation } from "wouter";
import { useToken } from "@/services/TokenContext";
import styles from "./CommonLayout.module.css";

export const CommonLayout = ({ children }: React.PropsWithChildren<{}>) => {
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
                            <LoggedInLinks role={tokenState.role} logOut={logOut} />
                        )}
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

type LoggedInLinksProps = {
    role: string;
    logOut: () => void;
};

const LoggedInLinks = ({ role, logOut }: LoggedInLinksProps) => (
    <>
        <div className={styles.navLinksLeft}>
            <Link className={styles.navLink} href="/">
                Main Page
            </Link>
            {role === "ADMIN" && (
                <>
                    <Link className={styles.navLink} href="/fields/create">
                        Create Field
                    </Link>
                    <Link className={styles.navLink} href="/fields/management">
                        Manage Fields
                    </Link>
                </>
            )}
            {role === "USER" && (
                <>
                    <Link className={styles.navLink} href="/fields">
                        View Fields
                    </Link>
                    <Link className={styles.navLink} href="/matches">
                        Matches
                    </Link>
                    <Link className={styles.navLink} href="/tournaments">
                        Tournaments
                    </Link>
                </>
            )}
            <Link className={styles.navLink} href="/search">
                Search Users
            </Link>
        </div>
        <div className={styles.navLinksRight}>
            {role === "USER" && (
                <>
                    <Link className={styles.navLink} href="/my-reservations">
                        My Reservations
                    </Link>
                    <Link className={styles.navLink} href="/my-matches">
                        My Matches
                    </Link>
                </>
            )}
            <Link className={styles.navLink} href="/profile">
                My Profile
            </Link>
            <button onClick={logOut} className={styles.navLink}>
                Log out
            </button>
        </div>
    </>
);