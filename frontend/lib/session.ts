import 'server-only';

import { cookies } from 'next/headers';

export async function saveSession(jwtToken: string) {
    const cookieStore = await cookies();
    cookieStore.set('session', jwtToken, {
        httpOnly: true,
        secure: process.env.SECURE_COOKIES === 'true',
        domain: process.env.COOKIE_DOMAIN,
        expires: new Date(Date.now() + 24 * 60 * 60 * 1000),
    });
    // console.debug(`Saved session: ${jwtToken}`);
}

export async function getSession() {
    const cookieStore = await cookies();
    return cookieStore.get('session');
}

export async function deleteSession() {
    const cookieStore = await cookies()
    cookieStore.delete('session')
}
