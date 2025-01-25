'use client';

import { useState, useEffect } from 'react';
import {
    LuLayoutDashboard,
    LuBoxes,
    LuChartBar,
    LuSettings,
    LuPanelLeftClose,
    LuPanelLeftOpen,
    LuLogOut
} from 'react-icons/lu';
import NavLink from './NavLink';
import styles from './Sidebar.module.scss';
import { logout } from '@/lib/actions';
import NavButton from './NavButton';
import GradientText from './GradientText';
import Image from 'next/image';

const navigationItems = [
    { icon: LuLayoutDashboard, label: 'Dashboard', url: '/app/dashboard' },
    { icon: LuBoxes, label: 'Model', url: '/app/model' },
    { icon: LuChartBar, label: 'Statistics', url: '/app/stats' },
    { icon: LuSettings, label: 'Settings', url: '/app/settings' },
];

export default function Sidebar() {
    const [isOpen, setIsOpen] = useState(false);

    useEffect(() => {
        setIsOpen(false);
    }, []);

    return (
        <header className={`${styles.sidebar} ${isOpen ? styles.open : styles.closed}`}>
            <div className={styles.titleContainer}>
                <Image
                    src="/app-logo.png"
                    alt="Magical Drones Logo"
                    width={40}
                    height={40}
                    priority
                />
                <h1><GradientText startColor="magenta" endColor="orange" direction="to bottom right">Magical Drones</GradientText></h1>
            </div>
            <button
                className={styles.toggleButton}
                onClick={() => setIsOpen(!isOpen)}
                aria-label={isOpen ? 'Close sidebar' : 'Open sidebar'}
            >
                {isOpen ? <LuPanelLeftClose size={20} /> : <LuPanelLeftOpen size={20} />}
            </button>

            <nav>
                <ul>
                    {navigationItems.map((item) => (
                        <NavLink
                            key={item.label}
                            icon={item.icon}
                            label={item.label}
                            url={item.url}
                            iconProps={{ size: 24 }}
                        />
                    ))}
                    <NavButton
                        icon={LuLogOut}
                        label="Log out"
                        onClick={logout}
                        iconProps={{ size: 24 }}
                    />
                </ul>
            </nav>

        </header>
    );
}
