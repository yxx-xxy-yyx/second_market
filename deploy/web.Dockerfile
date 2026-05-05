FROM node:20-alpine AS web-build
WORKDIR /app
COPY SecondMarket-Vue/package.json SecondMarket-Vue/package-lock.json ./SecondMarket-Vue/
RUN cd SecondMarket-Vue && npm ci
COPY SecondMarket-Vue ./SecondMarket-Vue
RUN cd SecondMarket-Vue && npm run build

FROM nginx:1.27-alpine
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=web-build /app/SecondMarket-Vue/dist /usr/share/nginx/html
