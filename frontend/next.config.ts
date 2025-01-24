import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: 'http',
                hostname: 'localhost',
                port: '8080',
                pathname: '/v1/**',
            },
        ],
    },
    // eslint: {
    //   ignoreDuringBuilds: true,
    // },
    typescript: {
        ignoreBuildErrors: true,
    },
    async redirects() {
        return [
          {
            source: '/app',
            destination: '/app/dashboard',
            permanent: true,
          },
        ]
      },
};

export default nextConfig;
