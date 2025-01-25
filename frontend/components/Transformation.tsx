'use client';

import { useState } from 'react';
import { parseDateTime } from '@/lib/utils';
import styles from './Transformation.module.scss';
import Image from 'next/image';
import LoadingBox from './LoadingBox';
import { TransformationData } from '@/lib/types';
import { makeInference } from '@/lib/actions';
import Spinner from './Spinner';

interface TransformationProps {
    transformation: TransformationData;
}

export default function Transformation({ transformation }: TransformationProps) {
    const [isLoading, setIsLoading] = useState(true);
    const [isGenerating, setIsGenerating] = useState(false);
    const [isSourceDownloading, setIsSourceDownloading] = useState(false);
    const [isTransformedDownloading, setIsTransformedDownloading] = useState(false);
    const sourceImageSrc = `/api/imageProxy?uuid=${transformation.sourceImageUuid}`;
    const transformedImageSrc = `/api/imageProxy?uuid=${transformation.transformedImageUuid}`;

    async function handleDownload(imageSrc: string, setDownloading: (state: boolean) => void, prefix: string) {
        try {
            setDownloading(true);
            const response = await fetch(imageSrc);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `${prefix}-${transformation.transformedImageUuid}.jpg`;
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


    // todo: handle errors better
    async function sendInferenceRequest() {
        setIsGenerating(true);
        console.log('Generation started');
        await makeInference(Number(transformation.id));
        console.log('Generation complete')
        setIsGenerating(false);
        // todo: the page should not reload; add transformation object to state and useEffect to fetch it
        window.location.reload();
    }

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>{transformation.title}</h2>
            {/* todo: create a SpinnerButton component */}
            <button className={styles.button} onClick={sendInferenceRequest} disabled={isGenerating}>
                {isGenerating ? (
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <Spinner size="small" />
                        <span style={{ marginLeft: '0.5rem' }}>Generating...</span>
                    </div>
                ) : (
                    'Generate'
                )}
            </button>
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
                            unoptimized
                        />
                    </div>
                    <button
                        className={styles.button}
                        onClick={() => handleDownload(sourceImageSrc, setIsSourceDownloading, 'original')}
                        disabled={isSourceDownloading}
                    >
                        {isSourceDownloading ? 'Downloading...' : 'Download original'}
                    </button>
                </div>
                <div className={styles.imageWrapper}>
                    <h3>Generated Image</h3>
                    <div className={styles.image}>
                        {transformation.transformedImageUuid ? (
                            <>
                                {isLoading && <LoadingBox text="Loading image..." />}
                                <Image
                                    src={transformedImageSrc}
                                    alt="Transformed image"
                                    fill
                                    style={{ objectFit: 'contain' }}
                                    sizes='(max-width: 768px) 100vh, 50vw'
                                    onLoad={() => setIsLoading(false)}
                                    unoptimized
                                />
                            </>
                        ) : (
                            <p className={styles.imgPlaceholderText}>Not generated yet.</p>
                        )}
                    </div>
                    {transformation.transformedImageUuid && (
                        <button
                            className={styles.button}
                            onClick={() => handleDownload(transformedImageSrc, setIsTransformedDownloading, 'generated')}
                            disabled={isTransformedDownloading}
                        >
                            {isTransformedDownloading ? 'Downloading...' : 'Download generated'}
                        </button>
                    )}
                </div>
            </div>
            <div className={styles.description}>
                <h3>Description</h3>
                <p>{transformation.description}</p>
            </div>
            <div className={styles.metadata}>
                <p>Generation started: {transformation.startedAt ? parseDateTime(transformation.startedAt).toLocaleString() : '---'}</p>
                <p>Generation completed: {transformation.completedAt ? parseDateTime(transformation.completedAt).toLocaleString() : '---'}</p>
            </div>
        </div>
    );
}