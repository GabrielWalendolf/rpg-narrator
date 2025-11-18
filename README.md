# RPG Narrador IA - Sistema de Jogo com Inteligência Artificial

Um sistema web de jogo de RPG com rolagem de dados e narração adaptativa usando **IA Generativa (Google Gemini)**, desenvolvido com **Spring Boot**, **Thymeleaf**, **Spring Data JPA** e **Spring AI**.

---

## ✅ Status Final do Projeto

| Requisito | Status | Observação |
|-----------|--------|------------|
| **Spring Framework** | ✅ Implementado | Spring Boot 3.3.0 e Spring MVC. |
| **Thymeleaf** | ✅ Implementado | Templates `index.html` e `game.html` funcionais. |
| **Spring Data JPA** | ✅ Implementado | Entidades (`Session`, `GameAction`) persistidas. |
| **Banco Relacional** | ✅ Implementado | H2 Database em memória (`create-drop`). |
| **IA Generativa (Gemini)** | ✅ Implementado | Chamada do modelo Gemini 2.0. |
| **Spring AI** | ✅ Implementado | Integração nativa via `ChatClient` (v1.1.0). |

---

Claro\! Com base no seu pedido, o seu **README.md** está atualizado, limpo e pronto para ser usado no GitHub, com as instruções de execução a partir do passo 3 (Configurar e Executar).

-----

## 📋 Pré-requisitos

- **Java 17** (Obrigatório para evitar erros de compilação com Lombok).
- Maven 3.6+.
- **Chave de API do Google Gemini** (necessária para rodar o projeto).

## 🚀 Como Executar

### 1\. Obter Chave Gemini

1.  Acesse [Google AI Studio](https://aistudio.google.com/app/apikeys).
2.  Clique em "Create API Key".
3.  Copie a chave gerada.

### 2\. Configurar e Executar (Windows PowerShell)

Para rodar o projeto, você deve definir a chave de API como uma **variável de ambiente** na sua sessão do PowerShell, pois o Spring Boot lê o valor a partir dela.

Execute os comandos abaixo na pasta raiz do projeto (`rpg-correto/rpg-correto`), substituindo `SUA_CHAVE_AQUI` pelo valor copiado:

```powershell
# 1. Define a variável de ambiente APENAS nesta sessão
$env:GEMINI_API_KEY="SUA_CHAVE_AQUI"

# 2. Compila, baixa todas as dependências e inicia o servidor Tomcat
mvn clean spring-boot:run
```

### 3\. Acessar a Aplicação

Abra seu navegador em: `http://localhost:8080`.

-----

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão (Corrigida) | Propósito |
| :--- | :--- | :--- |
| **Spring Boot** | 3.3.0 | Framework principal. |
| **Spring AI** | 1.1.0 | Integração oficial e moderna com a API. |
| **Lombok** | 1.18.36 | Redução de código boilerplate. |
| **Thymeleaf** | 3.x | Templates HTML. |
| **H2 Database** | 2.x | Banco de dados em memória para desenvolvimento. |
| **Google GenAI** | 2.0-flash | Modelo de IA para narração. |

## 📝 Endpoints

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| GET | `/` | Página inicial. |
| POST | `/session/create` | Cria nova sessão, gera o cenário inicial (via IA) e redireciona para a página do jogo. |
| GET | `/session/{sessionId}` | Carrega o estado atual da sessão e o histórico. |
| POST | `/session/{sessionId}/action` | Executa a ação, rola o dado, gera a narração (via IA) e persiste o novo turno. |




