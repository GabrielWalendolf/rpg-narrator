# RPG Narrador IA - Versão Completa com Spring

Um sistema web de jogo de RPG com rolagem de dados e narração adaptativa usando **IA Generativa (Gemini)**, desenvolvido com **Spring Boot**, **Thymeleaf**, **Spring Data JPA** e **Spring AI**.

## ✅ Requisitos Atendidos

- ✅ **Framework Spring** - Spring Boot 3.5.7
- ✅ **Telas web com Thymeleaf** - Templates HTML com Thymeleaf
- ✅ **Persistência de dados com Spring Data JPA** - Entidades e repositórios
- ✅ **Banco de dados relacional** - MySQL/H2
- ✅ **IA Generativa (Gemini)** - Spring AI integrado
- ✅ **Spring AI** - Integração completa com Google Generative AI

## 🎮 Funcionalidades

- Criação de sessões de RPG com temas diversos
- Rolagem de dados (d4, d6, d8, d10, d12, d20)
- Narração automática gerada por IA (Gemini)
- Memória de contexto completa (histórico de ações e narrações)
- Persistência de dados em banco relacional
- Interface web com Thymeleaf

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Chave de API do Google Gemini (gratuita)

## 🚀 Como Executar

### 1. Obter Chave Gemini

1. Acesse [Google AI Studio](https://aistudio.google.com/app/apikeys)
2. Clique em "Create API Key"
3. Copie a chave gerada

### 2. Extrair o arquivo ZIP

```bash
unzip rpg-correto.zip
cd rpg-correto
```

### 3. Configurar a Chave Gemini

**Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=sua-chave-aqui
mvn spring-boot:run
```

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="sua-chave-aqui"
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY=sua-chave-aqui
mvn spring-boot:run
```

### 4. Acessar a aplicação

Abra seu navegador em: `http://localhost:8080`

## 📁 Estrutura do Projeto

```
rpg-correto/
├── src/
│   └── main/
│       ├── java/br/edu/univille/poo/rpg/
│       │   ├── RpgApplication.java
│       │   ├── controller/
│       │   │   └── RpgController.java
│       │   ├── entity/
│       │   │   ├── Session.java
│       │   │   └── GameAction.java
│       │   ├── repository/
│       │   │   ├── SessionRepository.java
│       │   │   └── GameActionRepository.java
│       │   └── service/
│       │       ├── AiService.java
│       │       └── SessionService.java
│       └── resources/
│           ├── application.yml
│           ├── templates/
│           │   ├── index.html
│           │   └── game.html
│           └── static/css/
│               └── style.css
└── pom.xml
```

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Spring Boot | 3.5.7 | Framework web |
| Thymeleaf | 3.x | Templates HTML |
| Spring Data JPA | 3.x | Persistência |
| Hibernate | 6.x | ORM |
| MySQL Driver | 8.0.33 | Banco de dados |
| H2 Database | 2.x | Banco em memória (dev) |
| Spring AI | 1.0.0-M1 | IA Generativa |
| Google Generative AI | 1.0.0 | API Gemini |
| Lombok | 1.x | Redução de boilerplate |

## 📊 Fluxo da Aplicação

1. **Usuário acessa** `http://localhost:8080`
2. **Escolhe tema, dificuldade e classe**
3. **Clica em "Começar Aventura"**
4. **IA Gemini gera cenário inicial** (via Spring AI)
5. **Cenário é salvo no banco de dados**
6. **Usuário descreve uma ação**
7. **Sistema rola dados**
8. **IA Gemini gera narração** considerando:
   - Cenário inicial
   - Histórico de ações anteriores
   - Histórico de narrações anteriores
   - Resultado da rolagem atual
9. **Ação e narração são persistidas** no banco
10. **Ciclo continua**

## 🗄️ Banco de Dados

### Tabelas

#### sessions
```sql
CREATE TABLE sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    theme VARCHAR(255) NOT NULL,
    difficulty VARCHAR(255) NOT NULL,
    player_class VARCHAR(255) NOT NULL,
    initial_scenario LONGTEXT,
    current_context LONGTEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### game_actions
```sql
CREATE TABLE game_actions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    dice_type VARCHAR(10) NOT NULL,
    dice_result INT NOT NULL,
    action_description LONGTEXT NOT NULL,
    narration LONGTEXT NOT NULL,
    action_order INT NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);
```

## 🤖 Integração Spring AI

O projeto usa **Spring AI** para integração com Google Gemini:

```java
@Service
public class AiService {
    private final ChatClient chatClient;
    
    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String generateNarration(String context, String action, int result, String dice) {
        return chatClient.prompt(buildPrompt(...))
            .call()
            .content();
    }
}
```

## 🔧 Configuração Spring AI

Em `application.yml`:
```yaml
spring:
  ai:
    google:
      genai:
        api-key: ${GEMINI_API_KEY}
        chat:
          options:
            model: gemini-2.0-flash
            temperature: 0.9
```

## 📝 Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/` | Página inicial |
| POST | `/session/create` | Cria nova sessão |
| GET | `/session/{sessionId}` | Carrega sessão |
| POST | `/session/{sessionId}/action` | Executa ação |

## 🐛 Troubleshooting

### Erro: "Port 8080 already in use"
Mude a porta em `application.yml`:
```yaml
server:
  port: 8081
```

### Erro: "Gemini API key not configured"
Verifique se a variável de ambiente está definida:
```bash
echo %GEMINI_API_KEY%  # Windows
echo $GEMINI_API_KEY   # Linux/Mac
```

### Erro: "Failed to call Gemini API"
- Verifique se sua chave Gemini é válida
- Confirme que você tem conexão com a internet
- Verifique se o modelo `gemini-2.0-flash` está disponível

## 📞 Suporte

Para dúvidas ou problemas, consulte:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [Google Gemini API](https://ai.google.dev/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)

## 📄 Licença

MIT License
