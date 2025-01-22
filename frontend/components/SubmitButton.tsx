import { ButtonHTMLAttributes } from 'react';
import styles from './SubmitButton.module.scss';

interface SubmitButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    isLoading?: boolean;
}

export default function SubmitButton({
    children,
    isLoading,
    disabled,
    ...props
}: SubmitButtonProps) {
    return (
        <button
            type="submit"
            className={styles.button}
            disabled={disabled || isLoading}
            {...props}
        >
            {isLoading ? (
                <span className={styles.loading}>Loading...</span>
            ) : (
                children
            )}
        </button>
    );
} 