package Server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class DatabaseManager {
    private EntityManagerFactory emf;
    private Class<User> clazz;

    public void add(User user){
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(user);
        transaction.commit();
        em.close();
        System.out.println("added");
    }

    public User find(int id) throws NullPointerException{
        EntityManager em = emf.createEntityManager();
        System.out.println("starting finding id=" + id);
        User user = em.find(clazz, id);
        System.out.println("found");
        em.close();
        return user;
    }

    public User findByToken(String token) throws NullPointerException{
        EntityManager em = emf.createEntityManager();
        User user = em.createQuery("select u from User u where u.lastToken = :token", User.class)
                .setParameter("token", token)
                .getSingleResult();
        em.close();
        return user;
    }

    public void delete(User user){
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(em.merge(user));
        transaction.commit();
        em.close();
    }

    public List<User> findAll(){
        EntityManager em = emf.createEntityManager();
        List<User> users = em.createQuery("select u from User u", User.class)
                .getResultList();
        em.close();
        return users;
    }

    public User findByUsername(String username) throws NullPointerException {
        EntityManager em = emf.createEntityManager();
        String command = "SELECT u FROM User u WHERE u.username = :usr";
        Query query = em.createQuery(command, User.class);
        query.setParameter("usr", username);
        User user = (User)query.getResultList().get(0);
        System.out.println(user);
        em.close();
        return user;
    }

    public void generateSampleData(){
        User user1 = new User("admin",
                Base64.getEncoder().encodeToString("admin".getBytes(StandardCharsets.UTF_8)),
                Role.ADMIN, Policy.AccessLevel5);
        User user2 = new User(
                "user",
                Base64.getEncoder().encodeToString("user".getBytes(StandardCharsets.UTF_8)),
                Role.USER,
                Policy.AccessLevel1
        );
        add(user1);
        add(user2);
    }

    public void updateToken(int id, String newToken){
        EntityManager em = emf.createEntityManager();
        User user = em.find(clazz, id);
        em.getTransaction().begin();
        user.setLastToken(newToken);
        em.getTransaction().commit();
        em.close();
    }

    public void nullToken(int id){
        EntityManager em = emf.createEntityManager();
        User user = em.find(clazz, id);
        em.getTransaction().begin();
        user.setLastToken(null);
        em.getTransaction().commit();
        em.close();
    }
}
