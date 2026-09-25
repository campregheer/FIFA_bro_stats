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
O repositório inclui um Blueprint em `render.yaml` para configurar o serviço Docker. No Render, crie/import o Blueprint apontando para este repositório e informe as variáveis secretas pedidas:

- `DATABASE_URL`: URL JDBC externa do PostgreSQL, no formato `jdbc:postgresql://HOST:5432/DB?sslmode=require` (use o host externo do Neon).
- `DATABASE_USERNAME`: usuário do banco.
- `DATABASE_PASSWORD`: senha do banco.
- `CORS_ALLOWED_ORIGINS`: domínio final do Vercel, por exemplo `https://seu-projeto.vercel.app`, sem barra no final. Se houver mais de uma origem, separe-as por vírgulas.

O Blueprint ativa o perfil `prod`; o Render injeta `PORT` e o Spring usa esse valor. O Flyway aplica as migrações V1–V5 na inicialização. Não configure `ddl-auto=update` em produção. Guarde as credenciais somente nas variáveis do serviço, nunca em arquivos versionados.

Se preferir criar o serviço manualmente: escolha **Web Service → Docker**, branch `main`, Dockerfile `backend/Dockerfile` e contexto Docker `backend`. Configure as mesmas quatro variáveis acima e `SPRING_PROFILES_ACTIVE=prod`.

### Frontend (Vercel)
Importe o mesmo repositório como um projeto separado no Vercel e configure:

- **Root Directory:** `frontend`.
- **Framework Preset:** Vite.
- **Build Command:** `npm run build`.
- **Output Directory:** `dist`.
- **Environment Variable:** `VITE_API_URL=https://SEU-SERVICO.onrender.com` (URL base do backend, sem caminho `/api`).

O `frontend/vercel.json` inclui o rewrite necessário para recarregar rotas da aplicação sem receber 404. Depois do primeiro deploy, copie o domínio Vercel final para `CORS_ALLOWED_ORIGINS` no Render e faça redeploy do backend. Se mudar `VITE_API_URL`, gere um novo deploy do frontend, pois a variável é incorporada durante o build.

### Ordem de publicação
1. Confirme que o PostgreSQL/Neon está acessível externamente e faça backup se já tiver dados.
2. Publique o backend no Render e aguarde o Flyway concluir as migrações.
3. Publique o frontend no Vercel usando a URL pública do backend.
4. Atualize `CORS_ALLOWED_ORIGINS` no Render com o domínio do frontend e faça redeploy.
5. Teste a navegação, listagem e cadastro de jogadores, partidas, criação de campeonato, sorteio, placares, mata-mata e exclusão.

## 🤝 Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

## 📄 Licença

Este projeto está licenciado sob a licença MIT.

## 📞 Contato

Daniel Campregher Junior - [LinkedIn](https://www.linkedin.com/in/daniel-campregher-junior-101482352/) <!-- Substitua pelo seu perfil do LinkedIn -->

---
