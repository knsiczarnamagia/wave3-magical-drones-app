import PageTitle from "@/components/PageTitle";
import LoginForm from "@/components/LoginForm";
import { Suspense } from "react";

export default function LoginPage() {
    return (
        <>
            <PageTitle>Log into the app!</PageTitle>
            <Suspense fallback={<p>Loading...</p>}>
                <LoginForm />
            </Suspense>
        </>
    );
}
