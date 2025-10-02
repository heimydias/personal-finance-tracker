FROM node:22-alpine AS build

WORKDIR /app

# Cache de dependências
COPY package*.json ./
RUN npm ci

# Build
COPY . .
RUN npm run build

FROM nginx:alpine

# Adicionar curl para healthcheck
RUN apk add --no-cache curl

# Copiar arquivos buildados
COPY --from=build /app/build /usr/share/nginx/html

# Copiar configuração nginx
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Remover source maps (segurança)
RUN rm -rf /usr/share/nginx/html/*.map

# Ajustar permissões para usuário nginx (já existe na imagem)
RUN chown -R nginx:nginx /usr/share/nginx/html \
    && chown -R nginx:nginx /var/cache/nginx \
    && chown -R nginx:nginx /var/log/nginx \
    && chown -R nginx:nginx /etc/nginx/conf.d \
    && touch /var/run/nginx.pid \
    && chown -R nginx:nginx /var/run/nginx.pid

USER nginx

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]