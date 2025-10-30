import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

/**
 * Middleware to handle authentication and access control for admin routes
 * 
 * Rules:
 * 1. If user accesses /admin and is not authenticated -> redirect to /auth/login
 * 2. If user accesses /admin and is authenticated but not admin -> redirect to /
 * 3. If user is authenticated and tries to access /auth/* -> redirect to /
 * 4. If user is on mobile and tries to access /admin or /auth -> redirect to /
 */
export function middleware(request: NextRequest) {
    const response = NextResponse.next()

    // Add performance headers for faster loading
    response.headers.set('X-DNS-Prefetch-Control', 'on')

    // Enable early hints for critical resources
    if (request.nextUrl.pathname === '/') {
        response.headers.set('Link', [
            '</images/logo.png>; rel=preload; as=image',
            '<https://fonts.googleapis.com>; rel=preconnect',
            '<https://fonts.gstatic.com>; rel=preconnect; crossorigin',
        ].join(', '))
    }

    // Optimize for static assets
    if (request.nextUrl.pathname.startsWith('/_next/static/')) {
        response.headers.set('Cache-Control', 'public, max-age=31536000, immutable')
    }

    // Add server timing header for debugging
    if (process.env.NODE_ENV === 'development') {
        response.headers.set('Server-Timing', 'middleware;dur=0')
    }

    // Get the authentication token from cookies (try multiple possible cookie names)
    const authToken = request.cookies.get('access_token')?.value;

    // Parse the user authentication status
    let isAuthenticated = false
    let isAdmin = false
    let userRole = null

    if (authToken) {
        try {
            // Decode JWT payload (second part)
            const payload = authToken.split('.')[1]
            const decodedPayload = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))

            // Check if token is valid and not expired
            const currentTime = Math.floor(Date.now() / 1000)
            if (decodedPayload.exp && decodedPayload.exp > currentTime) {
                // Use userId from the decoded token (based on your JWT structure)
                isAuthenticated = !!decodedPayload.userId || !!decodedPayload.id || !!decodedPayload.sub
                userRole = decodedPayload.role
                isAdmin = userRole === 'admin'
            }
        } catch (error) {
            // If parsing fails, user is not authenticated
            console.error('Error parsing auth token:', error)
            isAuthenticated = false
            isAdmin = false
        }
    }

    const pathname = request.nextUrl.pathname

    // Debug logging for development
    if (process.env.NODE_ENV === 'development') {
        console.log('Middleware Debug:', {
            pathname,
            isAuthenticated,
            isAdmin,
            userRole,
            hasToken: !!authToken,
        })
    }

    // Check if user is on mobile (simplified check based on user agent)
    const userAgent = request.headers.get('user-agent') || ''
    const isMobile = /mobile|android|iphone|ipad/i.test(userAgent)

    // TEMPORARILY DISABLED: If on mobile, redirect to home page for both admin and auth routes
    // ONLY redirect mobile users, not desktop users
    if (isMobile && (pathname.startsWith('/admin') || pathname.startsWith('/auth'))) {
        return NextResponse.redirect(new URL('/', request.url))
    }

    // Check if user is accessing admin routes
    if (pathname.startsWith('/admin')) {
        // If not authenticated, redirect to login page
        if (!isAuthenticated) {
            // return NextResponse.redirect(new URL('/auth/login', request.url))
        }

        // If authenticated but not admin, redirect to home page
        if (!isAdmin) {
            // return NextResponse.redirect(new URL('/', request.url))
        }
    }
    if (pathname.startsWith('/cv-builder') || pathname.startsWith('/my-cvs')) {
        // If not authenticated, redirect to login page
        if (!isAuthenticated) {
            return NextResponse.redirect(new URL('/auth/login', request.url))
        }
    }
    if (pathname.startsWith('/settings') || pathname.startsWith('/my-cvs')) {
        // If not authenticated, redirect to login page
        if (!isAuthenticated) {
            return NextResponse.redirect(new URL('/auth/login', request.url))
        }
    }

    // Check if authenticated user is trying to access auth pages
    if (isAuthenticated && pathname.startsWith('/auth')) {
        // Redirect authenticated users away from auth pages
        // If admin, redirect to admin dashboard, otherwise to home
        const redirectUrl = isAdmin ? '/admin' : '/'
        return NextResponse.redirect(new URL(redirectUrl, request.url))
    }

    return response
}

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico (favicon file)
         */
        '/((?!api|_next/static|_next/image|favicon.ico).*)',
    ],
}