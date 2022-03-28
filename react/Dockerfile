FROM node:16
WORKDIR /app
COPY ./react/ml-runner/package.json /app
COPY ./react/ml-runner/package-lock.json /app
RUN npm ci --production
COPY ./react/ml-runner/build /app/build
COPY ./react/ml-runner/public /app/public
COPY ./react/ml-runner/src /app/src
COPY ./react/ml-runner/tsconfig.json /app/tsconfig.json

# RUN npm run build --production
# RUN npm install -g serve

EXPOSE 3000
CMD ["npm", "start"]

# FROM nginx:1.21.0-alpine as production
# ENV NODE_ENV production
# COPY --from=build /app/build /usr/share/nginx/html
# # Add your nginx.conf
# COPY nginx.conf /etc/nginx/conf.d/default.conf
# # Expose port
# EXPOSE 80
# # Start nginx
# CMD ["nginx", "-g", "daemon off;"]