# SoulMove API — Sprint 4 (Domain Driven Design using Java)

Equipe **Un-glitch Tech** — Turma 1TDSPV — FIAP 2026

API REST em Spring Boot do SoulMove: cadastro de usuários, meios de transporte
(ônibus e metrô) e viagens, com cálculo de CO₂ evitado e Pontos Soul Up.

- Documentação completa: `docs/Documentacao_SoulMove_Sprint4.pdf`
- Deploy: _(preencher com a URL do Render após publicar)_

## Como rodar

Requisitos: **JDK 17** ou superior e Maven (o IntelliJ já traz o Maven embutido).

### 1. Sem Oracle (banco H2 em memória, para testes rápidos)

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080` com duas pessoas e três transportes de exemplo.
Os dados são perdidos quando a aplicação é encerrada.

### 2. Com o Oracle da FIAP

1. Execute `database/script_banco_soulmove.sql` no SQL Developer (cria tabelas, sequences e dados de exemplo).
2. Defina as variáveis de ambiente e suba a aplicação:

```bash
# Windows (PowerShell)
$env:DB_URL="jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"
$env:DB_USUARIO="RM000000"
$env:DB_SENHA="sua_senha"
mvn spring-boot:run

# Linux / macOS
export DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
export DB_USUARIO=RM000000
export DB_SENHA=sua_senha
mvn spring-boot:run
```

No IntelliJ: *Run > Edit Configurations > Environment variables* e informe as três variáveis.

### 3. Testar os endpoints

Com a API no ar:

```bash
bash testes/testar_api.sh http://localhost:8080
```

O script percorre todos os endpoints (CRUD completo, erros 400/404, resgate de pontos e calculadora).

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Status da API e do banco |
| GET | `/usuarios` | Lista usuários |
| GET | `/usuarios/{id}` | Busca usuário |
| POST | `/usuarios` | Cadastra usuário |
| PUT | `/usuarios/{id}` | Altera usuário |
| DELETE | `/usuarios/{id}` | Remove usuário (sem viagens) |
| POST | `/usuarios/{id}/resgates` | Resgata Pontos Soul Up |
| GET | `/transportes` | Lista meios de transporte |
| GET | `/transportes/{id}` | Busca meio de transporte |
| POST | `/transportes` | Cadastra ônibus ou metrô |
| PUT | `/transportes/{id}` | Altera meio de transporte |
| DELETE | `/transportes/{id}` | Remove meio de transporte (sem viagens) |
| GET | `/viagens` | Lista viagens (`?idUsuario=` filtra por usuário) |
| GET | `/viagens/{id}` | Busca viagem |
| POST | `/viagens` | Registra viagem, calcula CO₂ e credita pontos |
| PUT | `/viagens/{id}` | Altera viagem e recalcula a recompensa |
| DELETE | `/viagens/{id}` | Remove viagem e estorna os pontos |
| GET | `/impacto?kmSemanais=` | Calculadora de impacto do site |

Exemplos de corpo JSON estão em `testes/testar_api.sh` e na documentação.

## Estrutura

```
src/main/java/br/com/soulmove
├── SoulMoveApplication.java   # classe principal
├── connection/                # ConnectionFactory (JDBC)
├── model/                     # Pessoa, Usuario, MeioTransporte, Onibus, Metro, Viagem
├── dao/                       # UsuarioDAO, MeioTransporteDAO, ViagemDAO (CRUD)
├── bo/                        # regras de negócio e validações
├── dto/                       # objetos recebidos no JSON (TransporteDTO, ViagemDTO, ResgateDTO)
├── resource/                  # endpoints REST
├── filter/                    # CorsFilter
├── exception/                 # exceções e TratadorDeExcecoes (@RestControllerAdvice)
└── config/                    # InicializadorBanco (cria o H2 local)
```

## Deploy no Render

1. Suba este projeto em um repositório no GitHub.
2. No Render: *New > Web Service*, conecte o repositório e escolha **Docker** (o `Dockerfile` já está no projeto).
3. Em *Environment*, cadastre `DB_URL`, `DB_USUARIO` e `DB_SENHA` com os dados do Oracle da FIAP.
4. Após o deploy, abra `https://<nome-do-servico>.onrender.com/` — deve responder `"bancoDisponivel": true`.

O arquivo `render.yaml` também permite criar o serviço via *Blueprint*.
