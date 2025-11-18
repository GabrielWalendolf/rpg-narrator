package br.edu.univille.poo.rpg.service;

import br.edu.univille.poo.rpg.entity.GameAction;
import br.edu.univille.poo.rpg.entity.Session;
import br.edu.univille.poo.rpg.repository.GameActionRepository;
import br.edu.univille.poo.rpg.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GameActionRepository gameActionRepository;
    private final AiService aiService;
    private final Random random = new Random();

    public SessionService(SessionRepository sessionRepository, GameActionRepository gameActionRepository, AiService aiService) {
        this.sessionRepository = sessionRepository;
        this.gameActionRepository = gameActionRepository;
        this.aiService = aiService;
    }

    /**
     * Cria uma nova sessão de RPG
     */
    public Session createSession(String theme, String difficulty, String playerClass) {
        // Gerar cenário inicial com IA
        String initialScenario = aiService.generateInitialScenario(theme, difficulty, playerClass);

        // Criar entidade de sessão
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setTheme(theme);
        session.setDifficulty(difficulty);
        session.setPlayerClass(playerClass);
        session.setInitialScenario(initialScenario);
        session.setCurrentContext(initialScenario);

        return sessionRepository.save(session);
    }

    /**
     * Obtém uma sessão pelo ID
     */
    public Optional<Session> getSessionBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    /**
     * Executa uma ação na sessão
     */
    public GameAction executeAction(Session session, String diceType, String actionDescription) {
        // Gerar resultado aleatório
        int maxValue = extractMaxValue(diceType);
        int result = random.nextInt(maxValue) + 1;

        // Obter histórico de ações
        List<GameAction> previousActions = gameActionRepository.findBySessionOrderByActionOrder(session);

        // Construir contexto completo
        String fullContext = buildFullContext(session, previousActions);

        // Gerar narração com IA
        String narration = aiService.generateNarration(fullContext, actionDescription, result, diceType);

        // Criar ação
        GameAction action = new GameAction();
        action.setSession(session);
        action.setDiceType(diceType);
        action.setDiceResult(result);
        action.setActionDescription(actionDescription);
        action.setNarration(narration);
        action.setActionOrder(previousActions.size() + 1);

        // Salvar ação
        GameAction savedAction = gameActionRepository.save(action);

        // Atualizar contexto da sessão
        session.setCurrentContext(narration);
        sessionRepository.save(session);

        return savedAction;
    }

    /**
     * Constrói o contexto completo da sessão
     */
    private String buildFullContext(Session session, List<GameAction> previousActions) {
        StringBuilder context = new StringBuilder();

        context.append("Cenário Inicial: ").append(session.getInitialScenario()).append("\n\n");

        if (!previousActions.isEmpty()) {
            context.append("Histórico de Ações:\n");
            for (int i = 0; i < previousActions.size(); i++) {
                GameAction action = previousActions.get(i);
                context.append((i + 1)).append(". Ação: ").append(action.getActionDescription()).append("\n");
                context.append("   Resultado: ").append(action.getDiceResult()).append(" em um ").append(action.getDiceType()).append("\n");
                context.append("   Narração: ").append(action.getNarration()).append("\n\n");
            }
        }

        return context.toString();
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
