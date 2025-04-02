# GraphQL API (SPQR) - Exemplo de Consultas e Mutações

Este documento fornece exemplos de consultas (queries) e mutações (mutations) para a API GraphQL com a biblioteca **GraphQL SPQR** (baseada em anotações, sem necessidade de SDL).

---

## 📌 Endereço do Servidor GraphQL

```
http://localhost:8080/graphql
```

---

### 1️⃣ Consulta de Estudante por ID

Essa consulta busca os detalhes de um estudante específico com base no `id` fornecido. Além disso, filtra as matérias com base no nome da disciplina.

#### Exemplo GraphQL:
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

#### Exemplo JSON (para Postman):
```json
{
  "query": "query { getStudent(id: \"1\") { id firstName lastName email fullName learningSubjects(subjectNameFilter: Java) { id subjectName marksObtained } } }"
}
```

✅ **Campos retornados:**
- `fullName`: Campo gerado dinamicamente no backend.
- `learningSubjects`: Lista de disciplinas filtradas por nome, usando o `SubjectNameFilter`.

---

## 📌 Mutações (Mutations)

### 2️⃣ Criar um Novo Estudante

#### Exemplo GraphQL:
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

#### Exemplo JSON:
```json
{
  "query": "mutation { createStudent(studentRequest: { firstName: \"John\", lastName: \"Doe\", email: \"john.doe@example.com\", street: \"123 Main St\", city: \"New York\", subjectsLearning: [ { subjectName: \"Java\", marksObtained: 85.5 }, { subjectName: \"MySQL\", marksObtained: 90.0 } ] }) { id firstName lastName email learningSubjects { subjectName marksObtained } } }"
}
```

---

### 3️⃣ Atualizar Estudante

#### Exemplo GraphQL:
```graphql
mutation {
    updateStudent(
        id: "1", 
        studentRequest: {
            firstName: "Jane"
            lastName: "Smith"
            email: "jane.smith@example.com"
            street: "456 Elm St"
            city: "San Francisco"
            subjectsLearning: [
                {
                    subjectName: "Spring Boot"
                    marksObtained: 92.0
                },
                {
                    subjectName: "GraphQL"
                    marksObtained: 88.5
                }
            ]
        }
    ) {
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

#### Exemplo JSON:
```json
{
  "query": "mutation { updateStudent(id: \"1\", studentRequest: { firstName: \"Jane\", lastName: \"Smith\", email: \"jane.smith@example.com\", street: \"456 Elm St\", city: \"San Francisco\", subjectsLearning: [ { subjectName: \"Spring Boot\", marksObtained: 92.0 }, { subjectName: \"GraphQL\", marksObtained: 88.5 } ] }) { id firstName lastName email learningSubjects { subjectName marksObtained } } }"
}
```

---

### 4️⃣ Excluir Estudante

#### Exemplo GraphQL:
```graphql
mutation {
    deleteStudent(id: "1")
}
```

#### Exemplo JSON:
```json
{
  "query": "mutation { deleteStudent(id: \"1\") }"
}
```

---

## 🛠️ Como Testar

Use ferramentas como:

- **Postman**: Selecione o método `POST`, aponte para `http://localhost:8080/graphql` e envie o JSON com a query/mutation.
- **Insomnia** ou **GraphQL Playground**: Para experiências interativas.

---

🧠 **Tecnologia**: Este projeto usa **GraphQL SPQR**, que permite anotar as classes Java para expor campos e métodos como schema GraphQL dinamicamente.
