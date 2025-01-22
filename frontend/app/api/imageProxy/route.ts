import { callWithErrors } from "@/lib/api-client";
import { NextResponse } from "next/server";

export async function GET(req: Request) {
    const url = new URL(req.url);
    const searchParams = url.searchParams;
    const imageUuid = searchParams.get('uuid');

    if (!imageUuid) {
        return NextResponse.json({ error: 'Image UUID is required' }, { status: 400 });
    }

    try {
        const response = await callWithErrors(`/image/${imageUuid}/`, {
            method: 'GET',
        });
        if (!response.ok) {
            return NextResponse.json(
                { error: 'Failed to fetch image' },
                { status: response.status }
            );
        }
        const imageBuffer = await response.arrayBuffer();
        return new NextResponse(imageBuffer, {
            headers: {
                'Content-Type': 'image/jpeg',
                'Cache-Control': 'public, max-age=31536000, immutable',
            },
        });

    } catch (error) {
        console.error('Error fetching image:', error);
        return NextResponse.json(
            { error: 'Failed to fetch image' },
            { status: 500 }
        );
    }
}