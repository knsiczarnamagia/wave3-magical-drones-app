import Link from 'next/link';
import PageTitle from '@/components/PageTitle';
import styles from './not-found.module.scss';

export default function NotFound() {
    return (
        <main className={styles.main}>
            <PageTitle>Page not found!</PageTitle>
            <p className={styles.text}>Could not find the requested resource.</p>
            <Link href="/" className={styles.link}>Back to home</Link>
        </main>
    );
}
