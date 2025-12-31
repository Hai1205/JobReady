/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
  // Disable automatic static optimization to prevent issues with cookies
  experimental: {
    // Remove this if you're not using it
  },
};

export default nextConfig;
