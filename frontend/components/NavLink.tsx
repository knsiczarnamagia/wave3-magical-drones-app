'use client';

import Link from 'next/link';
import { IconBaseProps, IconType } from 'react-icons';
import styles from './NavLink.module.scss';
import { usePathname } from 'next/navigation';

interface NavLinkProps {
    icon: IconType;
    label: string;
    url: string;
    iconProps?: IconBaseProps;
}

export default function NavLink({ icon: Icon, label, url, iconProps }: NavLinkProps) {
    const pathname = usePathname();
    console.log('Pathname - url:', pathname, url);
    const isActive = pathname === url;
    console.log('isActive:', isActive);

    return (
        <li className={`${styles.navLink} ${isActive ? styles.focused : ''}`}>
            <Link
                href={url}
                className={`${styles.link}`}
            >
                <Icon {...iconProps} />
                <p>{label}</p>
            </Link>
        </li>
    );
}
