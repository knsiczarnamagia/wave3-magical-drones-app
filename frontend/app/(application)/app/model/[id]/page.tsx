import PageTitle from "@/components/PageTitle";
import Transformation from "@/components/Transformation";
import { getTransformation } from "@/lib/data";

export default async function TransformationPage({
    params,
}: {
    params: Promise<{ id: string }>
}) {
    const id = (await params).id;
    const transformation = await getTransformation(id);

    return (
        <>
            {/* <PageTitle title={`${transformation.title}`} /> */}
            <Transformation transformation={transformation} />
        </>
    );
}