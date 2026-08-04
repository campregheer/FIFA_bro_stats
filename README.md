# FIFA Bro Stats

Um dashboard full-stack para rastrear partidas, estatísticas de jogadores, rankings e análises de desempenho de jogos EA Sports FC/FIFA. Desenvolvido com Spring Boot para o backend, React para o frontend e PostgreSQL como banco de dados.

## 🚀 Tecnologias Utilizadas

Este projeto é construído com as seguintes tecnologias:

### Backend
- **Spring Boot 3.x**: Framework Java para construção de aplicações robustas e escaláveis.
- **Maven**: Ferramenta de automação de build e gerenciamento de dependências.
- **Spring Data JPA**: Para persistência de dados e interação com o banco de dados.
- **Flyway**: Para gerenciamento de migrações de banco de dados.
- **PostgreSQL**: Banco de dados relacional.
- **Lombok**: Para reduzir o código boilerplate.
- **Docker**: Para conteinerização da aplicação.

### Frontend
- **React**: Biblioteca JavaScript para construção de interfaces de usuário.
- **TypeScript**: Superset do JavaScript que adiciona tipagem estática.
- **Tailwind CSS**: Framework CSS utilitário para estilização rápida e responsiva.
- **Vite**: Ferramenta de build frontend rápida.
- **React Query**: Para gerenciamento de estado e cache de dados assíncronos.

### Banco de Dados
- **Neon**: PostgreSQL serverless e escalável.

### Deploy
- **Render**: Plataforma de deploy para o backend conteinerizado.
- **Vercel**: Plataforma de deploy para o frontend (assumindo, pois é comum com React/Vite).

## ✨ Funcionalidades

- Rastreamento de partidas de EA Sports FC/FIFA.
- Estatísticas detalhadas de jogadores.
- Rankings de jogadores.
- Análises de desempenho.
- Gerenciamento de jogadores e times.

## ⚙️ Configuração e Execução Local

### Pré-requisitos
- Java 17 ou superior
- Node.js (LTS) e npm/yarn
- Docker (opcional, para rodar o backend em container)
- Conta Neon (para o banco de dados PostgreSQL)

### Backend
1. Clone o repositório:
   ```bash
   git clone https://github.com/campregheer/FIFA_bro_stats.git
   cd FIFA_bro_stats/backend
   ```
2. Configure o banco de dados PostgreSQL no Neon e obtenha as credenciais (URL, usuário, senha).
3. Crie um arquivo `application-dev.properties` em `src/main/resources` (se não existir) e configure as variáveis de ambiente:
   ```properties
   spring.datasource.url=jdbc:postgresql://[SUA_URL_NEON]
   spring.datasource.username=[SEU_USUARIO_NEON]
   spring.datasource.password=[SUA_SENHA_NEON]
   spring.jpa.hibernate.ddl-auto=update # Ou 'create' para criar o schema, 'validate' para validar
   spring.flyway.enabled=true
   spring.flyway.locations=classpath:db/migration
   server.error.include-stacktrace=never
   server.error.include-message=never
   cors.allowed-origins=http://localhost:3000 # Ou a porta do seu frontend local
   ```
4. Execute as migrações do Flyway (opcional, se `ddl-auto` não for `create`):
   ```bash
   ./mvnw flyway:migrate
   ```
5. Inicie a aplicação Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```
   O backend estará disponível em `http://localhost:8080`.

### Frontend
1. Navegue até a pasta do frontend:
   ```bash
   cd ../frontend
   ```
2. Instale as dependências:
   ```bash
   npm install # ou yarn install
   ```
3. Crie um arquivo `.env` na raiz do projeto frontend e configure a URL do backend:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```
4. Inicie a aplicação React:
   ```bash
   npm run dev # ou yarn dev
   ```
   O frontend estará disponível em `http://localhost:5173` (ou outra porta padrão do Vite).

## ☁️ Deploy

### Backend (Render)
O backend é conteinerizado com Docker e deployado no Render. As configurações essenciais incluem:
- **Repositório:** `https://github.com/campregheer/FIFA_bro_stats`
- **Root Directory:** `backend`
- **Environment:** Docker
- **Porta:** `8080` (exposta no Dockerfile)
- **Variáveis de Ambiente (Render Dashboard):**
  - `DATABASE_URL`: `jdbc:postgresql://[SUA_URL_NEON]`
  - `DATABASE_USERNAME`: `[SEU_USUARIO_NEON]`
  - `DATABASE_PASSWORD`: `[SUA_SENHA_NEON]`
  - `CORS_ALLOWED_ORIGINS`: `https://[URL_DO_SEU_FRONTEND_DEPLOYADO]`
  - `SPRING_PROFILES_ACTIVE`: `prod`

### Frontend (Vercel - Exemplo)
O frontend pode ser deployado em plataformas como Vercel ou Netlify. As configurações essenciais incluem:
- **Repositório:** `https://github.com/campregheer/FIFA_bro_stats` (apontando para a pasta `frontend`)
- **Build Command:** `npm run build`
- **Output Directory:** `dist`
- **Variáveis de Ambiente (Vercel Dashboard):**
  - `VITE_API_BASE_URL`: `https://[URL_DO_SEU_BACKEND_DEPLOYADO_NO_RENDER]`

## 🤝 Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

## 📄 Licença

Este projeto está licenciado sob a licença MIT.

## 📞 Contato

Daniel Campregher Junior - [LinkedIn](https://www.linkedin.com/in/daniel-campregher-junior-101482352/) <!-- Substitua pelo seu perfil do LinkedIn -->

---
