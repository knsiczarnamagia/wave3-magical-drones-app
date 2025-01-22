import { NextRequest, NextResponse } from 'next/server'
import { cookies } from 'next/headers'

const protectedRoutes = [
    '/app',
    // '/app/logout',
    '/app/dashboard',
    '/app/settings',
    '/app/model',
    /^\/app\/transform\/\d+$/
];
const publicRoutes = ['/app/login', '/'];

export default async function middleware(req: NextRequest): Promise<NextResponse> {
    const path = req.nextUrl.pathname;
    const isProtectedRoute = protectedRoutes.some(route => 
        typeof route === 'string' 
            ? route === path 
            : route.test(path)
    );
    // const isPublicRoute = publicRoutes.includes(path)

    const cookieStore = await cookies();

    if (isProtectedRoute && !cookieStore.has('session')) {
        return NextResponse.redirect(new URL('/forbidden', req.nextUrl))
    }

    return NextResponse.next()
}

export const config = {
    matcher: [
        '/((?!_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)'
    ],
}
