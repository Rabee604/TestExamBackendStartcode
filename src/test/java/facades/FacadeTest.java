package facades;

import dto.BoatDTO;
import dto.HarbourDTO;
import entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FacadeTest {
    Facade facade = Facade.getFacade(EMF_Creator.createEntityManagerFactoryForTest());
    static EntityManagerFactory emf;
    static EntityManager em;

    @BeforeEach
    void setUp() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        em = emf.createEntityManager();


        User user = new User("user", "test123");
        User admin = new User("admin", "test123");
        User both = new User("user_admin", "test123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        Boat boat1 = new Boat("Brand1", "Make1", "Boat1", "ImageURL1");
        Boat boat2 = new Boat("Brand2", "Make2", "Boat2", "ImageURL2");
        Boat boat3 = new Boat("Brand3", "Make3", "Boat3", "ImageURL3");

        Harbour harbour1 = new Harbour("Harbour1", "Address1", "20");
        Harbour harbour2 = new Harbour("Harbour2", "Address2", "15");

        Owner owner1 = new Owner("Owner1", "HomeAddress1", "12345678");
        Owner owner2 = new Owner("Owner2", "HomeAddress2", "87654321");
        Owner owner3 = new Owner("Owner3", "HomeAddress3", "43215678");


        owner1.addBoat(boat1);
        owner2.addBoat(boat2);
        owner3.addBoat(boat3);
        owner3.addBoat(boat1);
        boat1.setHarbour(harbour1);
        boat2.setHarbour(harbour1);


        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);

        try {
            em.getTransaction().begin();

            em.createQuery("delete from Boat").executeUpdate();
            em.createQuery("delete from Harbour").executeUpdate();
            em.createQuery("delete from Owner").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            em.createQuery("delete from User").executeUpdate();

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
        } finally {
            em.close();
        }

    }


    @AfterEach
    void tearDown() {
        emf.close();
    }

    @Test
    void getAllOwners() {

        System.out.println("Test for getting all owners");
        int expected = 3;
        int actual = facade.getAllOwners().size();
        assertEquals(expected, actual);

    }

    @Test
    void testGetBoatByHarbour() {
        System.out.println("Testing BoatByHarbour()");
        int expected = 2;
        int actual = facade.getBoatsByHarbour("harbour1").size();
        assertEquals(expected, actual);
    }

    @Test
    void testGetOwnerByBoat() {
        System.out.println("Testing testGetOwnerByBoat");
        int expected = 2;
        int actual = facade.getOwnersByBoat("boat1").size();
        assertEquals(expected, actual);
    }
    @Test
    void testCreatePerson() {
        System.out.println("Testing create()");
        Boat p = new Boat("Brand4", "Make4", "Boat4", "ImageURL4");
        BoatDTO pd1 = new BoatDTO(p);
        facade.create(pd1);
        int expected = 4;
        int actual = facade.getAllBoats().size();
        assertEquals(expected, actual);
    }
    @Test
    void setBoatHarbour() {
            System.out.println("Test for setting a boats harbour");

            int expected = 1;
            HarbourDTO harbour = facade.setBoatHarbour("Boat3","Harbour2");
            int actual = facade.getBoatsByHarbour("harbour2").size();

            assertEquals(expected, actual);
    }

    @Test
    void testDeleteBoatById()
    {
        System.out.println("Testing deleteBoatById()");
        int expected = 2;
        BoatDTO deletedBoat = facade.deleteBoatByID(1);
        int actual = facade.getAllBoats().size();
        assertEquals(expected, actual);

    }
}