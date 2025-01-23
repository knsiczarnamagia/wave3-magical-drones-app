import { IconType, IconBaseProps } from 'react-icons';
import Link from 'next/link';
import styles from './NavLink.module.scss';

interface NavLinkProps {
    icon: IconType;
    label: string;
    url: string;
    iconProps?: IconBaseProps;
}

export default function NavLink({ icon: Icon, label, url, iconProps }: NavLinkProps) {
    return (
        <li className={styles.navLink}>
            <Link href={url} className={styles.link}>
                <Icon {...iconProps} />
                <p>{label}</p>
            </Link>
        </li>
    );
}
