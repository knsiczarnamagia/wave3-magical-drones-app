import PageTitle from "@/components/PageTitle";
import Link from "next/link";
import styles from './page.module.scss';

export default function Forbidden() {
    return (
        <div className={styles.main} >
            <PageTitle>Access denied!</PageTitle>
            <Link href="/app/login" className={styles.link}>Please log in to continue</Link>
        </div>
    );
}