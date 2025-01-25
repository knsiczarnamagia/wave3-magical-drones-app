'use client';

import { fontPrimaryBold } from "@/lib/fonts";
import styles from './PageTitle.module.scss';
import { ReactNode } from 'react';

interface PageTitleProps {
    children: ReactNode;
    fontSize?: string;
    className?: string;
}

export default function PageTitle({ children, fontSize, className }: PageTitleProps) {
    return (
        <>
            <h1 
                className={`${styles.title} ${fontPrimaryBold.className} ${className || ''}`}
                style={fontSize ? { fontSize } : undefined}
            >
                {children}
            </h1>
        </>
    );
}
