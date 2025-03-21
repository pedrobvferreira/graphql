# GraphQL API - Exemplo de Consultas e Mutações

Este documento fornece exemplos de consultas (queries) e mutações (mutations) para a API GraphQL

---

## 📌 Endereço do Servidor GraphQL

```
http://localhost:8080/graphql
```

---

### 1️⃣ Consulta de Estudante por ID

Essa consulta busca os detalhes de um estudante específico com base no `id` fornecido. Além disso, filtra as matérias com base no nome da disciplina.

Exemplo GraphQL:
```graphql
query {
    getStudent(id: "1") {
        id
        firstName
        lastName
        email
        fullName
        learningSubjects(subjectNameFilter: Java) {
            id
            subjectName
            marksObtained
        }
    }
}
```

Exemplo JSON (corpo da requisição):
```JSON
{
  "query": "query { getStudent(id: "1") { id firstName lastName email fullName learningSubjects(subjectNameFilter: \"Java\") { id subjectName marksObtained } } }"
}
```

✅ **Explicação dos Campos:**
- `id`: Identificador único do estudante.
- `firstName`: Primeiro nome do estudante.
- `lastName`: Último nome do estudante.
- `email`: E-mail do estudante.
- `fullName`: Nome completo gerado dinamicamente.
- `learningSubjects(subjectNameFilter: Java)`: Lista de disciplinas filtradas pelo nome (nesse caso, "Java").
    - `id`: Identificador da disciplina.
    - `subjectName`: Nome da disciplina.
    - `marksObtained`: Notas obtidas pelo estudante na disciplina.

---

## 📌 Mutações (Mutations)

### 2️⃣ Criar um Novo Estudante

Essa mutação permite criar um novo estudante com informações pessoais e disciplinas que ele está estudando.

Exemplo GraphQL:
```graphql
mutation {
    createStudent(studentRequest: {
        firstName: "John"
        lastName: "Doe"
        email: "john.doe@example.com"
        street: "123 Main St"
        city: "New York"
        subjectsLearning: [
            {
                subjectName: "Java"
                marksObtained: 85.5
            },
            {
                subjectName: "MySQL"
                marksObtained: 90.0
            }
        ]
    }) {
        id
        firstName
        lastName
        email
        learningSubjects {
            subjectName
            marksObtained
        }
    }
}
```
Exemplo JSON:
```JSON
{
  "query": "mutation { createStudent(studentRequest: { firstName: \"John\", lastName: \"Doe\", email: \"john.doe@example.com\", street: \"123 Main St\", city: \"New York\", subjectsLearning: [ { subjectName: \"Java\", marksObtained: 85.5 }, { subjectName: \"MySQL\", marksObtained: 90.0 } ] }) { id firstName lastName email learningSubjects { subjectName marksObtained } } }"
}
```

✅ **Explicação dos Campos:**
- `firstName`: Primeiro nome do estudante.
- `lastName`: Sobrenome do estudante.
- `email`: E-mail do estudante.
- `street`: Endereço do estudante.
- `city`: Cidade do estudante.
- `subjectsLearning`: Lista de disciplinas estudadas pelo estudante.
    - `subjectName`: Nome da disciplina.
    - `marksObtained`: Nota obtida pelo estudante na disciplina.

🔹 **Retorno esperado**:
A mutação retorna o `id`, `firstName`, `lastName`, `email` e a lista de disciplinas com suas notas.

---

3️⃣

---
## 📌 Como Testar as Consultas

Para testar essas queries e mutations, você pode utilizar ferramentas como:

🔹 **Postman** – Permite testar GraphQL com uma interface amigável.  
🔹 **Insomnia** – Cliente avançado para testar APIs GraphQL.

Certifique-se de que o servidor GraphQL está rodando em `http://localhost:8080/graphql` antes de executar qualquer consulta.

---
