import PageTitle from "@/components/PageTitle";
import Transformation from "@/components/Transformation";
import { getTransformation } from "@/lib/data";

export default async function TransformationPage({
    params,
}: {
    params: Promise<{ id: string }>
}) {
    const id = (await params).id;
    let transformation = await getTransformation(id);
    transformation = JSON.parse(JSON.stringify(transformation));

    return (
        <>
            {/* <PageTitle title={`${transformation.title}`} /> */}
            <Transformation transformation={transformation} />
        </>
    );
}