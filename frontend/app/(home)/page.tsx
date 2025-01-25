import PageTitle from "@/components/PageTitle";
import Link from "next/link";
import styles from './page.module.scss';
import GradientText from "@/components/GradientText";

export default function Home() {
    return (
        <>
            <PageTitle className={styles.homeTitle}>
                <GradientText
                    startColor="magenta"
                    endColor="orange"
                    direction="to bottom right"
                    rotate
                >
                    Magical Drones
                </GradientText>
            </PageTitle>
            <Link href="/app/login" className={styles.link}>Log into the app</Link>
        </>
    );
}