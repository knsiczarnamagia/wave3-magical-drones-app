import GradientText from "@/components/GradientText";
import PageTitle from "@/components/PageTitle";
import RegisterForm from "@/components/RegisterForm";

export default function RegisterPage() {
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
            <RegisterForm />
        </>
    );
}