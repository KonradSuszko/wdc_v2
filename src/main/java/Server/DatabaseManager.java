package Server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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
        User user = em.createQuery("select u from User u where u.token = :token", User.class)
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
        System.out.println("users found");
        return users;
    }

    public User findByUsername(String username) throws NullPointerException {
        EntityManager em = emf.createEntityManager();
        User user = em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
        em.close();
        return user;
    }

    public void generateSampleData(){
        User user1 = new User("admin",
                Base64.getEncoder().encodeToString("admin".getBytes(StandardCharsets.UTF_8)),
                new ArrayList<>(List.of(Role.ADMIN, Role.STUFF, Role.USER)),
                new ArrayList<>(List.of(Policy.AccessLevel5)));
        add(user1);
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
