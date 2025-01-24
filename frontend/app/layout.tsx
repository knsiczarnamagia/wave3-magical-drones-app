import { fontPrimary } from '@/lib/fonts';
import './globals.scss';

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={fontPrimary.className}>
      <body>{children}</body>
    </html>
  );
}
