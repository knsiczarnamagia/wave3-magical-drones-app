'use server';

import { notFound, redirect } from "next/navigation";
import { getSession } from "./session";

const URL: string = 'http://localhost:8080/v1';

export async function getApiUrl(): Promise<string> {
    return process.env.API_URL || URL;
}

export async function call(path: string, options: RequestInit) {
    const response = await callWithErrors(path, options);
    if (response.ok) {
        return await response.json();
    }
    if ([401, 403].includes(response.status)) {
        redirect('/forbidden');
    }
    if (response.status === 404) {
        notFound();
    }
    redirect('/error');
}

export async function callWithErrors(path: string, options: RequestInit): Promise<Response> {
    const jwtToken = await getSession();
    // console.debug(`JWT token: ${JSON.stringify(jwtToken)}`);
    if (jwtToken) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${jwtToken.value}`
        };
    }

    const apiUrl = await getApiUrl();
    const url = path.startsWith('http') ? path : apiUrl + path;
    console.debug(`Calling ${url} with options: ${JSON.stringify(options)}`);
    const response = await fetch(url, options);
    return response;
}
