'use client';

import { useActionState } from 'react';
import { postRegistration } from '@/lib/actions';
import FormInput from './FormInput';
import SubmitButton from '@/components/SubmitButton';
import styles from '@/components/RegisterForm.module.scss';
import { RegisterFormState } from '@/lib/types';
import Link from 'next/link';

const initialState: RegisterFormState = undefined;

export default function RegisterForm() {
    const [state, formAction, isPending] = useActionState(postRegistration, initialState);

    return (
        <form action={formAction} className={styles.form}>
            <h2 className={styles.title}>Register</h2>
            {state?.message &&
                <p className={state.success ? styles.successMessage : styles.errorMessage}>
                    {state.message}
                </p>
            }
            <FormInput
                label="Username"
                name="username"
                error={state?.errors?.username}
                placeholder="Enter your username"
                autoComplete="username"
                required
            />
            <FormInput
                label="Password"
                type="password"
                name="password"
                error={state?.errors?.password}
                placeholder="Enter your password"
                autoComplete="current-password"
                required
            />
            <SubmitButton isLoading={isPending}>
                Register
            </SubmitButton>
            <p className={styles.loginInfo}>
                Already have an account?
                <Link href="/app/login">Log in</Link>
            </p>
        </form>
    );
}
