FROM node:17

WORKDIR /app
COPY ./ml-runner/package.json /app
COPY ./ml-runner/package-lock.json /app
RUN yarn install

COPY ./ml-runner/build /app/build
COPY ./ml-runner/public /app/public
COPY ./ml-runner/src /app/src
COPY ./ml-runner/tsconfig.json /app/tsconfig.json

EXPOSE 3000
CMD ["npm", "start"]