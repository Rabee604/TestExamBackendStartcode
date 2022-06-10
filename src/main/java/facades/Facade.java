package facades;

import dto.BoatDTO;
import dto.OwnerDTO;
import entities.Boat;
import entities.Harbour;
import entities.Owner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
        EntityManager em = emf.createEntityManager();
        try {
        List<Owner> ownerList=em.createQuery("select o from Owner o",Owner.class).getResultList();

        return OwnerDTO.getOwnerDTOs(ownerList);

        }finally {
            em.close();
        }



    }

   public List<BoatDTO> getBoatsByHarbour(Harbour harbour){
        return null;
   }
    public List<OwnerDTO> getOwnersByBoat(Boat boat){
        return null;
    }
}

