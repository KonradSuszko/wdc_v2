package Server;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue
    private Integer id;

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

    public Role getHighestRole(){
        if(roles.contains(Role.ADMIN)){
            return Role.ADMIN;
        }
        if(roles.contains(Role.STUFF)){
            return Role.STUFF;
        }
        if(roles.contains(Role.USER)){
            return Role.USER;
        }
        return null;
    }

    public Policy getHighestPolicy(){
        if(policies.contains(Policy.AccessLevel5)){
            return Policy.AccessLevel5;
        }
        if(policies.contains(Policy.AccessLevel4)){
            return Policy.AccessLevel4;
        }
        if(policies.contains(Policy.AccessLevel3)){
            return Policy.AccessLevel3;
        }
        if(policies.contains(Policy.AccessLevel2)){
            return Policy.AccessLevel2;
        }
        if(policies.contains(Policy.AccessLevel1)){
            return Policy.AccessLevel1;
        }
        return null;
    }
}
