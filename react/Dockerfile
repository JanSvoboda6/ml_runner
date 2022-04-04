FROM node:16 AS build

WORKDIR /app
COPY ./ml-runner/package.json /app
COPY ./ml-runner/package-lock.json /app
COPY ./ml-runner/public /app/public
COPY ./ml-runner/src /app/src
COPY ./ml-runner/tsconfig.json /app/tsconfig.json
COPY ./ml-runner/.env /app/.env
RUN npm install --only=production
RUN npm run build-ec2

FROM node:16-alpine
WORKDIR /app
COPY --from=build /app/build ./build
COPY --from=build /app/node_modules ./node_modules

EXPOSE 3000
RUN npm install -g serve
ENTRYPOINT [ "serve", "-s", "build"]