package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verification_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
