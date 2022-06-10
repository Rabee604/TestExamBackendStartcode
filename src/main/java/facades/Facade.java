package facades;

import dto.BoatDTO;
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
    public List<OwnerDTO> getOwnersByBoat(Boat boat){
        boat.getOwnerLists();

        return OwnerDTO.getOwnerDTOs(boat.getOwnerLists());
    }
}

