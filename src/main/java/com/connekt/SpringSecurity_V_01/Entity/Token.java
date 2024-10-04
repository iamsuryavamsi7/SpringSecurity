package com.connekt.SpringSecurity_V_01.Entity;

import com.connekt.SpringSecurity_V_01.Entity.Role.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "token_table"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @NotNull
    private boolean expired;

    @NotNull
    private boolean revoked;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "user_id"
    )
    private User user;

}
