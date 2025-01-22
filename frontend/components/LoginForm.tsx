'use client';

import { useActionState } from 'react';
import { useSearchParams } from 'next/navigation';
import { postLogin } from '@/lib/actions';
import FormInput from './FormInput';
import SubmitButton from '@/components/SubmitButton';
import styles from '@/components/LoginForm.module.scss';
import { PostLoginFormState } from '@/lib/types';
import Link from 'next/link';

const initialState: PostLoginFormState = undefined;

export default function LoginForm() {
    const [state, formAction, isPending] = useActionState(postLogin, initialState);
    const showLogoutMessage: boolean = useSearchParams().has('logout');

    return (
        <>
            <form action={formAction} className={styles.form}>
                <h2 className={styles.title}>Login</h2>
                {showLogoutMessage && (
                    <p className={styles.successMessage}>Logged out successfully!</p>
                )}
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
                    required />
                <FormInput
                    label="Password"
                    type="password"
                    name="password"
                    error={state?.errors?.password}
                    placeholder="Enter your password"
                    autoComplete="current-password"
                    required />
                <SubmitButton isLoading={isPending}>
                    Log In
                </SubmitButton>
                <p className={styles.registerInfo}>
                    Don&apos;t have an account?
                    <Link href="/app/register">Register</Link>
                </p>
            </form>
        </>
    );
}
