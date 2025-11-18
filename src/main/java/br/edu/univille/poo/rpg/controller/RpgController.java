package br.edu.univille.poo.rpg.controller;

import br.edu.univille.poo.rpg.entity.GameAction;
import br.edu.univille.poo.rpg.entity.Session;
import br.edu.univille.poo.rpg.repository.GameActionRepository;
import br.edu.univille.poo.rpg.service.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class RpgController {

    private final SessionService sessionService;
    private final GameActionRepository gameActionRepository;

    public RpgController(SessionService sessionService, GameActionRepository gameActionRepository) {
        this.sessionService = sessionService;
        this.gameActionRepository = gameActionRepository;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/session/create")
    public String createSession(
            @RequestParam String theme,
            @RequestParam String difficulty,
            @RequestParam String playerClass) {

        // Criar sessão
        Session session = sessionService.createSession(theme, difficulty, playerClass);

        // CORREÇÃO: Redirecionar para a URL correta com o ID
        return "redirect:/session/" + session.getSessionId();
    }

    @GetMapping("/session/{sessionId}")
    public String gameSession(@PathVariable String sessionId, Model model) {
        Optional<Session> sessionOpt = sessionService.getSessionBySessionId(sessionId);

        if (sessionOpt.isEmpty()) {
            return "redirect:/";
        }

        Session session = sessionOpt.get();
        List<GameAction> actions = gameActionRepository.findBySessionOrderByActionOrder(session);

        // CORREÇÃO: Mudamos o nome de "session" para "currentSession" para evitar conflito com Thymeleaf
        model.addAttribute("currentSession", session);
        model.addAttribute("actions", actions);

        return "game";
    }

    @PostMapping("/session/{sessionId}/action")
    public String executeAction(
            @PathVariable String sessionId,
            @RequestParam String diceType,
            @RequestParam String actionDescription) {

        Optional<Session> sessionOpt = sessionService.getSessionBySessionId(sessionId);

        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            sessionService.executeAction(session, diceType, actionDescription);
        }

        // CORREÇÃO: Redirecionar para a página da sessão para atualizar o histórico
        return "redirect:/session/" + sessionId;
    }
}