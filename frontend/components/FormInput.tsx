import { InputHTMLAttributes } from 'react';
import styles from './FormInput.module.scss';

interface FormInputProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
    error?: string[];
}

export default function FormInput({
    label,
    error,
    type = 'text',
    ...props
}: FormInputProps) {
    return (
        <div className={styles.inputContainer}>
            <label className={styles.label}>{label}
                <input
                    type={type}
                    className={`${styles.input} ${error ? styles.error : ''}`}
                    {...props}
                />
                {error && error.map((message, index) => (
                    <p key={index} className={styles.errorMessage}>
                        {message}
                    </p>
                ))}
            </label>
        </div>
    );
}
