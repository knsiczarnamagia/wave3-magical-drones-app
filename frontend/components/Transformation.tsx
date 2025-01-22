'use client';

import { useState } from 'react';
import { parseDateTime } from '@/lib/utils';
import styles from './Transformation.module.scss';
import Image from 'next/image';
import LoadingBox from './LoadingBox';

interface TransformationData {
    id: string;
    title: string;
    description: string;
    sourceImageUuid: string;
    transformedImageUuid: string;
    startedAt: string;
    completedAt: string;
}

interface TransformationProps {
    transformation: TransformationData;
}

export default function Transformation({ transformation }: TransformationProps) {
    const [isLoading, setIsLoading] = useState(true);
    const [isSourceDownloading, setIsSourceDownloading] = useState(false);
    const [isTransformedDownloading, setIsTransformedDownloading] = useState(false);
    const sourceImageSrc = `/api/imageProxy?uuid=${transformation.sourceImageUuid}`;
    const transformedImageSrc = `/api/imageProxy?uuid=${transformation.transformedImageUuid}`;

    const handleDownload = async (imageSrc: string, setDownloading: (state: boolean) => void) => {
        try {
            setDownloading(true);
            const response = await fetch(imageSrc);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `generated-${transformation.transformedImageUuid}.jpg`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Download failed:', error);
        } finally {
            setDownloading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>{transformation.title}</h2>
            <div className={styles.imagesContainer}>
                <div className={styles.imageWrapper}>
                    <h3>Original Image</h3>
                    <div className={styles.image}>
                        {isLoading && <LoadingBox text="Loading image..." />}
                        <Image
                            src={sourceImageSrc}
                            alt="Source image"
                            fill
                            style={{ objectFit: 'contain' }}
                            sizes='(max-width: 768px) 100vh, 50vw'
                            onLoad={() => setIsLoading(false)}
                        />
                    </div>
                    <button 
                        className={styles.downloadButton}
                        onClick={() => handleDownload(sourceImageSrc, setIsSourceDownloading)}
                        disabled={isSourceDownloading}
                    >
                        {isSourceDownloading ? 'Downloading...' : 'Download original'}
                    </button>
                </div>
                <div className={styles.imageWrapper}>
                    <h3>Generated Image</h3>
                    <div className={styles.image}>
                        {isLoading && <LoadingBox text="Loading image..." />}
                        <Image
                            src={transformedImageSrc}
                            alt="Transformed image"
                            fill
                            style={{ objectFit: 'contain' }}
                            sizes='(max-width: 768px) 100vw, 50vw'
                            onLoad={() => setIsLoading(false)}
                        />
                    </div>
                    <button 
                        className={styles.downloadButton}
                        onClick={() => handleDownload(transformedImageSrc, setIsTransformedDownloading)}
                        disabled={isTransformedDownloading}
                    >
                        {isTransformedDownloading ? 'Downloading...' : 'Download generated'}
                    </button>
                </div>
            </div>
            <div className={styles.description}>
                <h3>Description</h3>
                <p>{transformation.description}</p>
            </div>
            <div className={styles.metadata}>
                <p>Generation started: {parseDateTime(transformation.startedAt).toLocaleString()}</p>
                <p>Generation completed: {parseDateTime(transformation.completedAt).toLocaleString()}</p>
            </div>
        </div>
    );
}