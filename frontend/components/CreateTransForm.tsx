"use client";

import { useActionState } from "react";
import FormInput from "./FormInput";
import { createTransformation } from "@/lib/actions";
import SubmitButton from "./SubmitButton";
import styles from './CreateTransForm.module.scss';


const initialState = undefined;

export default function CreateTransForm() {
    const [state, formAction, isPending] = useActionState(createTransformation, initialState);

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
                error={state?.errors?.sourceImage}
                required
            />
            <FormInput
                label="Title"
                name="title"
                error={state?.errors?.title}
                maxLength={50}
                required
            />
            <FormInput
                label="Description"
                name="description"
                error={state?.errors?.description}
                maxLength={3000}
            />
            <SubmitButton isLoading={isPending}>
                Create
            </SubmitButton>
        </form>
    );
}