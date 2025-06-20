import { CommonLayout } from "@/components/CommonLayout/CommonLayout"
import { useLocation } from "wouter"
import styles from "./PlayerHomePage.module.css"

export const PlayerHomePage = () => {
  const [, navigate] = useLocation()

  return (
    <CommonLayout>
      <section className={styles.pageContainer}>
        <h1 className="text-center text-3xl font-bold mb-10 text-green-400">
          Welcome back, Player!
        </h1>

        <img
          src="/img/logo_2-no-background.webp"
          alt="Balonini Logo"
          className="mb-10"
          style={{
            minWidth: "4rem",
            minHeight: "4rem",
            maxWidth: "15rem",
            maxHeight: "15rem",
            display: "block",
          }}
        />

        <div className={styles.optionsGrid}>
          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Fields</div>
            <p className={styles.optionDescription}>
              Browse available fields and book your next match with ease.
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/fields")}
            >
              Go to Fields
            </button>
          </div>

          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Matches</div>
            <p className={styles.optionDescription}>
              Join open matches or create one and invite your friends!
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/matches")}
            >
              Go to Matches
            </button>
          </div>

          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Teams</div>
            <p className={styles.optionDescription}>
              Manage your team or find teammates to play with.
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/teams")}
            >
              Go to Teams
            </button>
          </div>

          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Tournaments</div>
            <p className={styles.optionDescription}>
              Discover upcoming tournaments or register your team!
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/tournaments")}
            >
              Go to Tournaments
            </button>
          </div>
        </div>
      </section>
    </CommonLayout>
  )
}
