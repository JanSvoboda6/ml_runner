FROM node:16

WORKDIR /app
COPY ./ml-runner/package.json /app
COPY ./ml-runner/package-lock.json /app
RUN npm install --only=production

COPY ./ml-runner/public /app/public
COPY ./ml-runner/src /app/src
COPY ./ml-runner/tsconfig.json /app/tsconfig.json
COPY ./ml-runner/.env /app/.env

EXPOSE 3000
ENTRYPOINT ["npm", "start"]