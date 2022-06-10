package entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boat")
public class Boat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String brand;
    private String make;
    private String name;
    private String image;
    @ManyToOne
    @JoinColumn(name = "harbour_id")
    private Harbour harbour;

    @ManyToMany(mappedBy = "boatLists")
    private List<Owner> ownerLists = new ArrayList<>();
    public Boat() {

    }


    public Boat(Long id, String brand, String make, String name, String image) {
        this.id = id;
        this.brand = brand;
        this.make = make;
        this.name = name;
        this.image = image;
    }

    public Boat(String brand, String make, String name, String image) {
        this.brand = brand;
        this.make = make;
        this.name = name;
        this.image = image;
    }
    public void addOwner(Owner owner){
        this.ownerLists.add(owner);
        if(!owner.getBoatLists().contains(this)){
            owner.addBoat(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Owner> getOwnerLists() {
        return ownerLists;
    }

    public void setOwnerLists(List<Owner> ownerLists) {
        this.ownerLists = ownerLists;
    }
}