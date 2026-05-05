const trimTrailingSlash = (value: string) => value.replace(/\/+$/, '')

const backendOriginRaw = process.env.VERCEL_BACKEND_ORIGIN || process.env.BACKEND_ORIGIN || ''
const backendOrigin = backendOriginRaw ? trimTrailingSlash(backendOriginRaw) : ''

const apiRewrite = backendOrigin
  ? { source: '/api/:path*', destination: `${backendOrigin}/api/:path*` }
  : { source: '/api/:path*', destination: '/api/:path*' }

export default {
  installCommand: 'cd SecondMarket-Vue && npm ci',
  buildCommand: 'cd SecondMarket-Vue && npm run build',
  outputDirectory: 'SecondMarket-Vue/dist',
  rewrites: [apiRewrite, { source: '/(.*)', destination: '/index.html' }]
}
