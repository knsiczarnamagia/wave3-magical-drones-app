"use client";

import { useActionState, useState } from "react";
import FormInput from "./FormInput";
import { callWithErrors } from "@/lib/api-client";
import { createTransformation } from "@/lib/actions";
import SubmitButton from "./SubmitButton";
import styles from './CreateTransForm.module.scss';
import { UploadImageResponse } from "@/lib/types";

interface FormData {
    title: string;
    description: string;
    sourceImage: string | null;
}
// interface ErrorState {
//     title: string[] | undefined;
//     description: string[] | undefined;
//     sourceImage: string[] | undefined;
//     message: string[] | undefined;
// }

const initialState = undefined;

export default function CreateTransForm() {
    const [state, formAction, isPending] = useActionState(createTransformation, initialState);

    // const [formState, setFormState] = useState<FormData>({
    //     title: '',
    //     description: '',
    //     sourceImage: null
    // });
    // const [errorState, setErrorState] = useState<ErrorState>({
    //     title: undefined,
    //     description: undefined,
    //     sourceImage: undefined,
    //     message: undefined
    // });

    // async function fileUpload(e: React.ChangeEvent<HTMLInputElement>) {
    //     const file = e.target.files?.[0];
    //     console.log(file);

    //     if (file) {
    //         const formData = new FormData();
    //         formData.append('sourceImg', file);

    //         try {
    //             const response = await callWithErrors('/image', {
    //                 method: 'POST',
    //                 body: formData,
    //             });

    //             if (!response.ok) {
    //                 setErrorState(prev => ({
    //                     ...prev, sourceImage: ['Upload failed!']
    //                 }));
    //             }

    //             const result: UploadImageResponse = await response.json();
    //             console.log('Upload success:', result);
    //             setFormState(prev => {
    //                 const newState = {
    //                     ...prev,
    //                     sourceImage: result.uuid
    //                 };
    //                 console.log('Updated form state:', newState);
    //                 return newState;
    //             });
    //         } catch (error) {
    //             console.error('Error uploading:', error);
    //         }
    //     }
    // }

    return (
        <form action={formAction} className={styles.form}>
            <h1 className={styles.title}>Create transformation</h1>
            {state?.message &&
                <p className={state.success ? styles.successMessage : styles.errorMessage}>
                    {state?.message}
                </p>
            }
            <FormInput
                label="Source image"
                name="sourceImage"
                type="file"
                // onChange={fileUpload}
                error={state?.errors?.sourceImage}
                required
            />
            <FormInput
                label="Title"
                name="title"
                // value={formState.title}
                // onChange={e => setFormState(prev => ({
                //     ...prev,
                //     title: e.target.value
                // }))}
                error={state?.errors?.title}
                maxLength={50}
                required
            />
            <FormInput
                label="Description"
                name="description"
                // value={formState.description}
                // onChange={e => setFormState(prev => ({
                //     ...prev,
                //     description: e.target.value
                // }))}
                error={state?.errors?.description}
                maxLength={3000}
            />
            <SubmitButton isLoading={isPending}>
                Create
            </SubmitButton>
        </form>
    );
}