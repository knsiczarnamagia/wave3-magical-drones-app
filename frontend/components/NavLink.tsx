'use client';

import Link from 'next/link';
import { IconBaseProps, IconType } from 'react-icons';
import styles from './NavLink.module.scss';
import { usePathname } from 'next/navigation';
import { useSidebar } from '@/contexts/SidebarContext';

interface NavLinkProps {
    icon: IconType;
    label: string;
    url: string;
    iconProps?: IconBaseProps;
}

export default function NavLink({ icon: Icon, label, url, iconProps }: NavLinkProps) {
    const pathname = usePathname();
    const isActive = pathname === url;
    const { setIsOpen } = useSidebar();

    const handleClick = () => {
        if (window.innerWidth < 768) {
            setIsOpen(false);
        }
    };

    return (
        <li className={`${styles.navLink} ${isActive ? styles.focused : ''}`}>
            <Link
                href={url}
                className={`${styles.link}`}
                onClick={handleClick}
            >
                <Icon {...iconProps} />
                <p>{label}</p>
            </Link>
        </li>
    );
}
