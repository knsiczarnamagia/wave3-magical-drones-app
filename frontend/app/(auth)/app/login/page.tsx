import PageTitle from "@/components/PageTitle";
import LoginForm from "@/components/LoginForm";
import { Suspense } from "react";
import GradientText from "@/components/GradientText";

export default function LoginPage() {
    return (
        <>
            <PageTitle>
                <GradientText
                    startColor="magenta"
                    endColor="orange"
                    direction="to bottom right"
                >
                    Magical Drones
                </GradientText>
            </PageTitle>
            <Suspense fallback={<p>Loading...</p>}>
                <LoginForm />
            </Suspense>
        </>
    );
}
