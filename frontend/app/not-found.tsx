import Link from 'next/link';
import PageTitle from '@/components/PageTitle';
import styles from './not-found.module.scss';
import { fontPrimary } from '@/lib/fonts';
import { cookies } from 'next/headers';

// todo: make this a client component?
export default async function NotFound() {

    const cookieStore = await cookies();
    const isAuthorized = cookieStore.has('session');

    return (
        <html lang="en" className={fontPrimary.className}>
            <body>
                <main className={styles.main}>
                    <PageTitle>Page not found!</PageTitle>
                    <p className={styles.text}>Could not find the requested resource.</p>
                    {isAuthorized && <Link href="/app" className={styles.link}>Back to app</Link>}
                    {!isAuthorized && <Link href="/" className={styles.link}>Back to home</Link>}
                </main>
            </body>
        </html>
    );
}
