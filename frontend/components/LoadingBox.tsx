import styles from './LoadingBox.module.scss';
import Spinner from './Spinner';

export default function LoadingBox({ text }: { text: string }) {
    return (
        <div className={styles.container}>
            <Spinner size="small" />
            <span className={styles.text}>{text}</span>
        </div>
    );
}
