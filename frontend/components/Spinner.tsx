import styles from './Spinner.module.scss';

interface SpinnerProps {
    size?: 'small' | 'medium' | 'large';
    color?: string;
}

export default function Spinner({ size = 'medium', color = 'currentColor' }: SpinnerProps) {
    return (
        <span className={`${styles.spinner} ${styles[size]}`} style={{ borderColor: color, borderTopColor: 'transparent' }} />
    );
} 