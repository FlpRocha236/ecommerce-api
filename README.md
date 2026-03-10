# 🛒 E-commerce API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-brightgreen)

API REST de e-commerce desenvolvida com Spring Boot seguindo boas práticas de mercado.

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.5.11
- Spring Data JPA + Hibernate
- PostgreSQL 15
- Swagger / OpenAPI 3
- JUnit 5 + Mockito
- Docker + Docker Compose
- Lombok

## 📦 Funcionalidades

- ✅ CRUD de Produtos com controle de estoque
- ✅ Carrinho de compras por cliente
- ✅ Gestão de Pedidos com controle de status
- ✅ Tratamento global de exceções
- ✅ Validação de dados de entrada
- ✅ Paginação e ordenação
- ✅ Documentação Swagger

## ⚙️ Como executar localmente

### Pré-requisitos
- Java 17+
- Docker Desktop

### Passo a passo
```bash
# Clone o repositório
git clone https://github.com/FlpRocha236/ecommerce-api.git
cd ecommerce-api

# Suba o banco de dados
docker-compose up -d

# Execute a aplicação
./mvnw spring-boot:run
```

### Acesse a documentação
```
http://localhost:8080/swagger-ui/index.html
```

## 🧪 Executar testes
```bash
./mvnw test
```

## 📁 Estrutura do projeto
```
src/main/java/com/flprocha/ecommerce_api/
├── controller/    ← Endpoints REST
├── service/       ← Regras de negócio
├── repository/    ← Acesso ao banco
├── entity/        ← Entidades JPA
├── dto/
│   ├── request/   ← Dados de entrada
│   └── response/  ← Dados de saída
├── exception/     ← Tratamento de erros
└── config/        ← Configurações
```

## 📌 Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/v1/products | Criar produto |
| GET | /api/v1/products | Listar produtos |
| GET | /api/v1/products/{id} | Buscar produto |
| PUT | /api/v1/products/{id} | Atualizar produto |
| DELETE | /api/v1/products/{id} | Desativar produto |
| POST | /api/v1/cart/add | Adicionar ao carrinho |
| GET | /api/v1/cart/{email} | Ver carrinho |
| POST | /api/v1/orders | Criar pedido |
| GET | /api/v1/orders/{id} | Buscar pedido |
| PATCH | /api/v1/orders/{id}/pay | Processar pagamento |

## 👨‍💻 Autor

**Felipe Rocha**  
[GitHub](https://github.com/FlpRocha236)