import { CommonLayout } from "@/components/CommonLayout/CommonLayout"
import { useLocation } from "wouter"
import styles from "./AdminHomePage.module.css"

export const AdminHomePage = () => {
  const [, navigate] = useLocation()

  return (
    <CommonLayout>
      <section className={styles.pageContainer}>
        <h1 className={styles.adminTitle}>Welcome back, Field Admin!</h1>

        <img
          src="/img/logo_2-no-background.webp"
          alt="Balonini Logo"
          className={styles.logo}
        />

        <div className={styles.optionsGrid}>
          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Create Field</div>
            <p className={styles.optionDescription}>
              Set up a new football field to allow players to reserve and match.
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/fields/create")}
            >
              Go to Create
            </button>
          </div>

          <div className={styles.optionCard}>
            <div className={styles.optionTitle}>Manage Fields</div>
            <p className={styles.optionDescription}>
              View, edit, or remove existing fields and manage reservations.
            </p>
            <button
              className={styles.optionButton}
              onClick={() => navigate("/fields/management")}
            >
              Go to Management
            </button>
          </div>
        </div>
      </section>
    </CommonLayout>
  )
}
