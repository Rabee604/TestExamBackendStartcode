package facades;

import dto.BoatDTO;
import dto.HarbourDTO;
import dto.OwnerDTO;
import entities.Boat;
import entities.Harbour;
import entities.Owner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class Facade implements Ifacade{
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Facade instance;

    public Facade() {
    }

    public static Facade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            em = emf.createEntityManager();
            instance = new Facade();
        }
        return instance;
    }
@Override
    public List<OwnerDTO> getAllOwners(){

        List<Owner> ownerList=em.createQuery("select o from Owner o",Owner.class).getResultList();

        return OwnerDTO.getOwnerDTOs(ownerList);





    }
    @Override
    public Boat getBoatByName(String name){
        Boat boat = em.createQuery("select b from Boat b where b.name= :name", Boat.class)
                .setParameter("name", name)
                .getSingleResult();
            return boat;
    }
    @Override
    public List<BoatDTO> getBoatsByHarbour(String harbour) {
        TypedQuery<Harbour> query= em.createQuery("select b from Harbour b where b.name= :harbour", Harbour.class);
               query.setParameter("harbour",harbour);
            Harbour harbour1 =query.getSingleResult();


        return BoatDTO.getBoatDTOs(harbour1.getBoatList());
    }
    @Override
    public List<OwnerDTO> getOwnersByBoat(String boat){
        TypedQuery<Boat> query = em.createQuery("select b from Boat b where b.name= :boat", Boat.class);
        query.setParameter("boat",boat);
        Boat boat1 = query.getSingleResult();
        return OwnerDTO.getOwnerDTOs(boat1.getOwnerLists());
    }

    public BoatDTO create(BoatDTO boatDTO){
        Boat boat= new Boat(boatDTO.getId(), boatDTO.getBrand(), boatDTO.getMake(), boatDTO.getName(), boatDTO.getImage());

        em.getTransaction().begin();
        em.persist(boat);
            em.getTransaction().commit();
        return new BoatDTO(boat);
    }
    public List<BoatDTO> getAllBoats(){
        List<Boat> boats= em.createQuery("SELECT b from Boat b", Boat.class).getResultList();


        return BoatDTO.getBoatDTOs(boats);
    }
    @Override
    public HarbourDTO setBoatHarbour(String boat, String harbour) {
        TypedQuery<Boat> typedQuery= em.createQuery("select b from Boat b where b.name= :boat", Boat.class);
        typedQuery.setParameter("boat",boat);
        Boat boat1 = typedQuery.getSingleResult();
        TypedQuery<Harbour> query= em.createQuery("select b from Harbour b where b.name= :harbour", Harbour.class);
        query.setParameter("harbour",harbour);
        Harbour harbour1 = query.getSingleResult();
        boat1.setHarbour(harbour1);
        em.getTransaction().begin();
        em.merge(boat1);
        em.getTransaction().commit();

        return new HarbourDTO(harbour1);
    }

    public BoatDTO deleteBoatByID(long id)  {
        Boat boat = em.find(Boat.class, id);

            em.getTransaction().begin();

            // removes Boat from owner
            boat.getOwnerLists().forEach( owner-> {
                em.remove(owner);
            });

            em.remove(boat);
            em.getTransaction().commit();

        return new BoatDTO(boat);
    }
}

