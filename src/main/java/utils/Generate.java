package utils;


import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Generate {

    public static void main(String[] args) {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        User user = new User("user", "test123");
        User admin = new User("admin", "test123");
        User both = new User("user_admin", "test123");
        Boat boat1 = new Boat("Brand1","Make1","Boat1","ImageURL1");
        Boat boat2 = new Boat("Brand2","Make2","Boat2","ImageURL2");
        Boat boat3 = new Boat("Brand3","Make3","Boat3","ImageURL3");

        Harbour harbour1 = new Harbour("Harbour1","Address1","20");
        Harbour harbour2 = new Harbour("Harbour2","Address2","15");

        Owner owner1 = new Owner("Owner1","HomeAddress1","12345678");
        Owner owner2 = new Owner("Owner2","HomeAddress2","87654321");
        Owner owner3 = new Owner("Owner3","HomeAddress3","43215678");

        owner1.addBoat(boat1);
        owner2.addBoat(boat2);
        owner3.addBoat(boat3);
        owner3.addBoat(boat1);
        boat1.setHarbour(harbour1);
        boat2.setHarbour(harbour1);
        em.getTransaction().begin();

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.persist(boat1);
        em.persist(boat2);
        em.persist(boat3);

        em.persist(harbour1);
        em.persist(harbour2);

        em.persist(owner1);
        em.persist(owner2);
        em.persist(owner3);
        em.getTransaction().commit();
    }

}
