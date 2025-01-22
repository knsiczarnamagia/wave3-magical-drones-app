import { IconType, IconBaseProps } from 'react-icons';
import styles from './NavButton.module.scss';

interface NavButtonProps {
    icon: IconType;
    label: string;
    onClick: () => void;
    iconProps?: IconBaseProps;
}

export default function NavButton({ icon: Icon, label, onClick, iconProps }: NavButtonProps) {
    return (
        <li className={styles.navButton}>
            <button onClick={onClick} className={styles.button}>
                <Icon {...iconProps} />
                <p>{label}</p>
            </button>
        </li>
    );
} 