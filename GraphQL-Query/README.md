# GraphQL API - Exemplo de Consultas e Mutações

Este documento fornece exemplos de consultas (queries) e mutações (mutations) para a API GraphQL

---

## 📌 Endereço do Servidor GraphQL

```
http://localhost:8080/graphql
```

---

## 📌 Consultas (Queries)

### 1️⃣ Consulta Simples

Essa consulta retorna os dados de `firstQuery` e `secondQuery`.

```graphql
query {
    firstQuery
    secondQuery
    fullName
}
```


## 📌 Como Testar as Consultas

Para testar essas queries e mutations, você pode utilizar ferramentas como:

🔹 **GraphiQL** – Interface web interativa para testar GraphQL.  
🔹 **Postman** – Permite testar GraphQL com uma interface amigável.  
🔹 **Insomnia** – Cliente avançado para testar APIs GraphQL.

Certifique-se de que o servidor GraphQL está rodando em `http://localhost:8080/graphql` antes de executar qualquer consulta.

---
