# GraphQL API - Exemplo de Queries

Este documento fornece exemplos de consultas (queries) para a API GraphQL

---

## 📌 Endereço do Servidor GraphQL

```
http://localhost:8080/graphql
```

---

## 📌 Consultas (Queries)

### 1️⃣ Consulta Simples

Essa consulta retorna os dados de `firstQuery` e `secondQuery`.

Exemplo GraphQL:
```graphql
query {
    firstQuery
    secondQuery
    fullName
}
```
Exemplo JSON:
```json
{
"query": "query { firstQuery secondQuery }"
}
```


## 📌 Como Testar as Consultas

Para testar essas queries e mutations, você pode utilizar ferramentas como:

🔹 **GraphiQL** – Interface web interativa para testar GraphQL.  
🔹 **Postman** – Permite testar GraphQL com uma interface amigável.  
🔹 **Insomnia** – Cliente avançado para testar APIs GraphQL.

Certifique-se de que o servidor GraphQL está rodando em `http://localhost:8080/graphql` antes de executar qualquer consulta.

---
