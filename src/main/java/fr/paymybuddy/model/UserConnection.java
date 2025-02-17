package fr.paymybuddy.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_connections")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnection {

    @EmbeddedId
    private UserConnectionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("connectionId")
    @JoinColumn(name = "connection_id", nullable = false)
    private User connection;
}
