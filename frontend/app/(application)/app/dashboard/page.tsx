import CardGrid from "@/components/CardGrid";
import PageTitle from "@/components/PageTitle";
import { Suspense } from "react";

export default async function DashboardPage() {
    return (
        <>
            <PageTitle>Dashboard</PageTitle>
            <Suspense fallback={<div style={{textAlign: 'center'}}>Loading...</div>}>
                <CardGrid />
            </Suspense>
        </>
    );
}
