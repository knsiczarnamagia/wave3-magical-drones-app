'use client';

import PageTitle from "@/components/PageTitle"
import { getBackendAppMetadata } from "@/lib/data";
import { BackendAppMetadata } from "@/lib/types";
import { useEffect, useState } from "react";

export default function SettingsPage() {
    const [metadata, setMetadata] = useState<BackendAppMetadata | null>(null);

    useEffect(() => {
        async function setBackendAppMetadata() {
            const metadata = await getBackendAppMetadata();
            setMetadata(metadata);
        }
        setBackendAppMetadata();
    }, []);

    return (
        <>
            <PageTitle>Settings</PageTitle>
            <h2>Backend service info:</h2>
            <ul>
                <li>Name: {metadata?.app.name || 'Loading...'}</li>
                <li>Description: {metadata?.app.description || 'Loading...'}</li>
                <li>Version: {metadata?.app.version || 'Loading...'}</li>
            </ul>
        </>
    );
}