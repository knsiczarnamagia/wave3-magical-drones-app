import React from "react";
import { fontPrimary } from "@/lib/fonts";
import styles from './layout.module.scss';

export default function HomeLayout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" className={fontPrimary.className}>
            <body>
                <main className={styles.main}>{children}</main>
            </body>
        </html>
    );
}