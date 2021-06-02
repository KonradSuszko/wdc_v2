package Server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String username;

    private String hashedPassword;

    @ElementCollection
    private List<Role> roles;

    @ElementCollection
    private List<Policy> policies;

    private String lastToken;

    public User(String username, String hashedPassword, List<Role> roles, List<Policy> policies) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.roles = roles;
        this.policies = policies;
        this.lastToken = null;
    }
}
