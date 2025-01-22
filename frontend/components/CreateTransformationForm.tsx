'use client';

import { useState, useCallback } from 'react';
import FormInput from './FormInput';
import { useDropzone } from 'react-dropzone';
import styles from './CreateTransformationForm.module.scss';
import { callWithErrors } from '@/lib/api-client';

interface FormData {
    title: string;
    description: string;
    sourceImage: string | null;
}

interface UploadImageResponse {
    uuid: string;
}

export default function CreateTransformationForm() {
    const [formState, setFormState] = useState<FormData>({
        title: '',
        description: '',
        sourceImage: null
    });
    const [isSubmitting, setIsSubmitting] = useState(false);

    const onDrop = useCallback(async (acceptedFiles: File[]) => {
        if (acceptedFiles.length === 0) return;
        console.log('acceptedFiles: ', acceptedFiles);
        const file = acceptedFiles[0];
        try {
            const formData = new FormData();
            formData.append('sourceImg', file);

            // If there's an existing image, delete it first
            if (formState.sourceImage) {
                await callWithErrors('/image', {
                    method: 'DELETE',
                    body: JSON.stringify({
                        uuid: formState.sourceImage
                    })
                });
            }

            const response = await callWithErrors('/image', {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Failed to upload image');

            const uploaded: UploadImageResponse = await response.json();
            setFormState(prev => ({ ...prev, sourceImage: uploaded.uuid }));
        } catch (error) {
            console.error('Failed to upload image:', error);
        }
    }, [formState.sourceImage]);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'image/*': ['.jpg']
        },
        maxFiles: 1
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!formState.sourceImage || isSubmitting) return;

        setIsSubmitting(true);
        try {
            const response = await callWithErrors('/transform', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formState),
            });

            if (!response.ok) throw new Error('Failed to create transformation');

            // Reset form after successful submission
            setFormState({
                title: '',
                description: '',
                sourceImage: null
            });
        } catch (error) {
            console.error('Failed to create transformation:', error);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <FormInput
                label="Title"
                value={formState.title}
                onChange={(e) => setFormState(prev => ({ ...prev, title: e.target.value }))}
                required
                maxLength={50}
            />
            <FormInput
                label="Description"
                value={formState.description}
                onChange={(e) => setFormState(prev => ({ ...prev, description: e.target.value }))}
                maxLength={3000}
            />
            <div
                {...getRootProps()}
                className={`${styles.dropzone} ${isDragActive ? styles.active : ''}`}
            >
                <input {...getInputProps()} />
                {formState.sourceImage ? (
                    <p>Image uploaded! Drop a new image to replace it.</p>
                ) : isDragActive ? (
                    <p>Drop the image here...</p>
                ) : (
                    <p>Drag and drop an image here, or click to select one</p>
                )}
            </div>
            <button
                type="submit"
                disabled={!formState.sourceImage || isSubmitting}
                className={styles.submitButton}
            >
                {isSubmitting ? 'Creating...' : 'Create Transformation'}
            </button>
        </form>
    );
} 