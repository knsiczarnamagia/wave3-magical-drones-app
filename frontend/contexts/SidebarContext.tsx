'use client';

import { createContext, useContext } from 'react';

interface SidebarContextType {
    setIsOpen: (isOpen: boolean) => void;
}

export const SidebarContext = createContext<SidebarContextType | null>(null);

export const useSidebar = () => {
    const context = useContext(SidebarContext);
    if (!context) {
        throw new Error('useSidebar must be used within a SidebarProvider');
    }
    return context;
};
