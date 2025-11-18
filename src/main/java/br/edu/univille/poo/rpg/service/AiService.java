package br.edu.univille.poo.rpg.service;

// NOVAS IMPORTAÇÕES DO SPRING AI
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class AiService {

    // CAMPO NOVO: ChatClient é injetado pelo Spring
    private final ChatClient chatClient;

    // CONSTRUTOR NOVO: Injeta o builder do ChatClient
    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Gera um cenário inicial para começar a sessão
     */
    public String generateInitialScenario(String theme, String difficulty, String playerClass) {

        // Usamos PromptTemplate para estruturar o prompt de forma mais limpa
        PromptTemplate promptTemplate = new PromptTemplate("""
                Crie um cenário inicial imersivo e envolvente para uma sessão de RPG de mesa.
                
                Tema: {theme}
                Dificuldade: {difficulty}
                Classe do Jogador: {playerClass}
                
                Gere um cenário que:
                1. Seja interessante e envolvente
                2. Apresente o mundo e a situação inicial
                3. Deixe claro o que o jogador pode fazer
                4. Tenha 2-3 parágrafos
                5. Termine com uma pergunta ou abertura para a ação do jogador
                """);

        String prompt = promptTemplate.render(
                Map.of(
                        "theme", theme,
                        "difficulty", difficulty,
                        "playerClass", playerClass
                )
        );

        // CHAMADA DA API USANDO SPRING AI
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * Gera uma narração usando Gemini API
     */
    public String generateNarration(String fullContext, String playerAction, int diceResult, String diceType) {
        int maxValue = extractMaxValue(diceType);
        double successPercentage = (double) diceResult / maxValue * 100;

        String successLevel;
        if (successPercentage >= 80) {
            successLevel = "Sucesso Crítico (Ação espetacular)";
        } else if (successPercentage >= 50) {
            successLevel = "Sucesso (Ação realizada conforme o esperado)";
        } else if (successPercentage >= 25) {
            successLevel = "Sucesso Parcial (Conseguiu, mas com um custo ou imprevisto)";
        } else {
            successLevel = "Falha (Ação não realizada e possíveis consequências negativas)";
        }

        // Usamos PromptTemplate para estruturar o prompt de forma mais limpa
        PromptTemplate promptTemplate = new PromptTemplate("""
                Você é um Mestre de RPG experiente narrando uma aventura contínua.
                Sua tarefa é gerar a narração do próximo turno mantendo TOTAL coerência com o histórico.
                
                === HISTÓRICO DA AVENTURA (MEMÓRIA) ===
                {fullContext}
                =======================================
                
                === TURNO ATUAL ===
                AÇÃO DO JOGADOR: {playerAction}
                DADO ROLADO: {diceResult} ({diceType})
                RESULTADO MECÂNICO: {successLevel}
                
                Instruções de Narração:
                1. Descreva a consequência direta da ação baseada no Nível de Sucesso.
                2. IMPORTANTE: Use elementos do Histórico (nomes, estados, locais) para manter a continuidade.
                3. Se houve falha, descreva as consequências negativas.
                4. Termine descrevendo a nova situação para o jogador reagir.
                5. Máximo de 3 parágrafos curtos.
                """);

        String prompt = promptTemplate.render(
                Map.of(
                        "fullContext", fullContext,
                        "playerAction", playerAction,
                        "diceResult", diceResult,
                        "diceType", diceType,
                        "successLevel", successLevel
                )
        );

        // CHAMADA DA API USANDO SPRING AI
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    // Método callGeminiApi foi removido, assim como os campos Gson, OkHttpClient e GEMINI_API_URL.
    // O bloco try-catch de conexão ainda é tratado indiretamente pelo Spring AI.


    /**
     * Extrai o valor máximo do tipo de dado
     */
    private int extractMaxValue(String diceType) {
        return switch(diceType) {
            case "d4" -> 4;
            case "d6" -> 6;
            case "d8" -> 8;
            case "d10" -> 10;
            case "d12" -> 12;
            case "d20" -> 20;
            default -> 20;
        };
    }
}