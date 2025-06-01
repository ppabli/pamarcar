FROM node:24 AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:24

WORKDIR /app
COPY --from=builder /app ./

RUN npm install -g serve

EXPOSE 4321
CMD ["serve", "dist"]
