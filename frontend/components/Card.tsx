'use client';

import Image from 'next/image';
import styles from './Card.module.scss';
import LoadingBox from './LoadingBox';
import { useState } from 'react';

interface CardProps {
    imageUuid: string;
    title: string;
    description?: string;
}

export default function Card({ imageUuid, title, description }: CardProps) {
    const [isLoading, setIsLoading] = useState(true);

    const imageSrc = `/api/imageProxy?uuid=${imageUuid}`;

    return (
        <div className={styles.card}>
            {imageSrc && (
                <div className={styles.imageContainer}>
                    {isLoading && <LoadingBox text="Loading..." />}
                    <Image
                        src={imageSrc}
                        alt={title}
                        fill
                        className={styles.image}
                        sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                        onLoad={() => setIsLoading(false)}
                        unoptimized
                    />
                </div>
            )}
            <div className={styles.content}>
                <h3 className={styles.title}>{title}</h3>
                <p className={styles.description}>{description}</p>
            </div>
        </div>
    );
} 