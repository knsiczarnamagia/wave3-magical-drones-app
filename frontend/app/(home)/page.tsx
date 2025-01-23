import PageTitle from "@/components/PageTitle";
import Link from "next/link";
import styles from './page.module.scss';
import GradientText from "@/components/GradientText";

export default function Home() {
    return (
        <>
            <PageTitle fontSize="2.75rem">
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