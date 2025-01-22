import { fontPrimary } from "@/lib/fonts";
import styles from './layout.module.scss';

export default function LoginLayout({ children }: { children: React.ReactNode }) {
    return (
      <html lang="en" className={fontPrimary.className}>
        <body>
          <main className={styles.main}>{children}</main>
        </body>
      </html>
    );
  }
  