import { call, callWithErrors } from "./api-client";
import { TransformationData } from "./types";


export async function getTransformations(): Promise<TransformationData[]> {
    const resBody: TransformationData[] = await call('/transform', {
        method: 'GET',
    });
    return resBody;
}

export async function getTransformation(id: string): Promise<TransformationData> {
    const resBody: TransformationData = await call(`/transform/${id}`, {
        method: 'GET',
    });
    return resBody;
}

export async function getTransformationImage(uuid: string): Promise<string> {
    const imageUrl = `/image/${uuid}`;
    const response = await callWithErrors(imageUrl, {
        method: 'GET',
    });
    let src = '';
    if (response.ok) {
        const image = await response.blob();
        src = URL.createObjectURL(image);
    }
    return src;
}
