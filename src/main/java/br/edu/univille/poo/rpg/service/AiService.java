package br.edu.univille.poo.rpg.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class AiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    /**
     * Gera um cenário inicial para começar a sessão
     */
    public String generateInitialScenario(String theme, String difficulty, String playerClass) {
        String prompt = "Crie um cenário inicial imersivo e envolvente para uma sessão de RPG de mesa.\n\n" +
                "Tema: " + theme + "\n" +
                "Dificuldade: " + difficulty + "\n" +
                "Classe do Jogador: " + playerClass + "\n\n" +
                "Gere um cenário que:\n" +
                "1. Seja interessante e envolvente\n" +
                "2. Apresente o mundo e a situação inicial\n" +
                "3. Deixe claro o que o jogador pode fazer\n" +
                "4. Tenha 2-3 parágrafos\n" +
                "5. Termine com uma pergunta ou abertura para a ação do jogador";

        return callGeminiApi(prompt);
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

        // PROMPT MELHORADO PARA CONTINUIDADE
        String prompt = "Você é um Mestre de RPG experiente narrando uma aventura contínua.\n" +
                "Sua tarefa é gerar a narração do próximo turno mantendo TOTAL coerência com o histórico.\n\n" +
                "=== HISTÓRICO DA AVENTURA (MEMÓRIA) ===\n" +
                fullContext + "\n" +
                "=======================================\n\n" +
                "=== TURNO ATUAL ===\n" +
                "AÇÃO DO JOGADOR: " + playerAction + "\n" +
                "DADO ROLADO: " + diceResult + " (" + diceType + ")\n" +
                "RESULTADO MECÂNICO: " + successLevel + "\n\n" +
                "Instruções de Narração:\n" +
                "1. Descreva a consequência direta da ação baseada no Nível de Sucesso.\n" +
                "2. IMPORTANTE: Use elementos do Histórico (nomes, estados, locais) para manter a continuidade.\n" +
                "3. Se houve falha, descreva as consequências negativas.\n" +
                "4. Termine descrevendo a nova situação para o jogador reagir.\n" +
                "5. Máximo de 3 parágrafos curtos.";

        return callGeminiApi(prompt);
    }

    /**
     * Chama a API Gemini
     */
    private String callGeminiApi(String prompt) {
        try {
            // Construir o corpo da requisição
            JsonObject requestBody = new JsonObject();
            JsonArray contentsArray = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray partsArray = new JsonArray();
            JsonObject part = new JsonObject();

            part.addProperty("text", prompt);
            partsArray.add(part);
            content.add("parts", partsArray);
            contentsArray.add(content);
            requestBody.add("contents", contentsArray);

            // Criar a requisição HTTP
            String url = GEMINI_API_URL + "?key=" + apiKey;
            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Executar a requisição
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string(); // Lê a resposta APENAS UMA VEZ aqui

                if (!response.isSuccessful()) {
                    // Imprime o erro detalhado no console para você ver o que aconteceu
                    System.err.println("ERRO DETALHADO GEMINI: " + responseBody);
                    return "Erro ao chamar API Gemini (" + response.code() + "): " + responseBody;
                }

                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                // Extrair o texto da resposta
                if (responseJson.has("candidates") && responseJson.getAsJsonArray("candidates").size() > 0) {
                    JsonObject candidate = responseJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
                    if (candidate.has("content") && candidate.getAsJsonObject("content").has("parts")) {
                        JsonArray parts = candidate.getAsJsonObject("content").getAsJsonArray("parts");
                        if (parts.size() > 0) {
                            return parts.get(0).getAsJsonObject().get("text").getAsString();
                        }
                    }
                }

                return "Não foi possível gerar narração (Resposta sem candidatos).";
            }

        } catch (IOException e) {
            return "Erro ao conectar com API Gemini: " + e.getMessage();
        } catch (Exception e) {
            return "Erro ao processar resposta da IA: " + e.getMessage();
        }
    }

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
