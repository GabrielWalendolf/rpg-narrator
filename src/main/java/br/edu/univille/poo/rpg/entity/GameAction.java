package br.edu.univille.poo.rpg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false)
    private String diceType;

    @Column(nullable = false)
    private Integer diceResult;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String actionDescription;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String narration;

    @Column(nullable = false)
    private Integer actionOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
