package facades;

import dto.BoatDTO;
import dto.OwnerDTO;
import entities.Boat;
import entities.Harbour;
import entities.Owner;

import java.util.List;

public interface Ifacade {
    List<OwnerDTO> getAllOwners();
    Boat getBoatByName(String name);
    List<BoatDTO> getBoatsByHarbour(String harbour);
     List<OwnerDTO> getOwnersByBoat(Boat boat);
}
