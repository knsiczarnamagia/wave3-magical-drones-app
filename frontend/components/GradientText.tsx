import { ReactNode } from 'react';
import styles from './GradientText.module.scss';

type GradientDirection = 'to right' | 'to left' | 'to top' | 'to bottom' | 'to bottom right' | 'to bottom left' | 'to top right' | 'to top left';

interface GradientTextProps {
    children: ReactNode;
    startColor: string;
    endColor: string;
    direction?: GradientDirection;
    rotate?: boolean;
}

export default function GradientText({ 
    children, 
    startColor, 
    endColor, 
    direction = 'to right',
    rotate = false 
}: GradientTextProps) {
    return (
        <span
            className={rotate ? styles.rotatingGradient : ''}
            style={{
                background: `linear-gradient(${direction}, ${startColor} 5%, ${endColor} 95%)`,
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                display: 'inline-block'
            }}
        >
            {children}
        </span>
    );
}