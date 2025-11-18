package br.edu.univille.poo.rpg.repository;

import br.edu.univille.poo.rpg.entity.GameAction;
import br.edu.univille.poo.rpg.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameActionRepository extends JpaRepository<GameAction, Long> {
    List<GameAction> findBySessionOrderByActionOrder(Session session);
}
