import { fontPrimary } from '@/lib/fonts';
import Sidebar from '@/components/Sidebar';
import styles from './layout.module.scss';
import '@/app/globals.scss';

export default function AppLayout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" className={fontPrimary.className}>
            <body>
                <div className={styles.container}>
                    <Sidebar />
                    <main className={styles.main}>
                        <div className={styles.scrollableContent}>
                            {children}
                        </div>
                    </main>
                </div>
            </body>
        </html>
    );
}
