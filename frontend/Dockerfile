FROM node:22 AS build
WORKDIR /app
COPY ./package.json ./package-lock.json ./
COPY public/ /app/public/
RUN npm install # Use npm install to install dependencies
RUN npm ci
COPY . .
RUN npm run build


FROM nginx:1.27.4 AS deploy
WORKDIR /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY run.sh /
COPY --from=build /app/dist/ .

CMD ["sh", "/run.sh"]