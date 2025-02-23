import Link from 'next/link';
import PageTitle from '@/components/PageTitle';
import { fontPrimary } from '@/lib/fonts';
import styles from './not-found.module.scss';
// import '@/app/globals.scss';

export default async function NotFound() {
    
    return (
        <html lang="en" className={fontPrimary.className}>
            <body>
                <main className={styles.main}>
                    <PageTitle>Page not found!</PageTitle>
                    <p className={styles.text}>Could not find the requested resource.</p>
                    <Link href="/" className={styles.link}>Back to home</Link>
                </main>
            </body>
        </html>
    );
}
