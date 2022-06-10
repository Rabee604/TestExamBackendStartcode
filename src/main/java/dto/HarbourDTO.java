package dto;

import entities.Harbour;
import entities.Owner;

import java.util.ArrayList;
import java.util.List;

public class HarbourDTO {

    private Long id;
    private String name;
    private String address;
    private String capacity;


    public HarbourDTO(Long id, String name, String address, String capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }

    public HarbourDTO(Harbour harbour) {
        this.id = harbour.getId();
        this.name = harbour.getName();
        this.address = harbour.getAddress();
        this.capacity = harbour.getCapacity();
    }

    public static List<HarbourDTO> getHarbourDTOs(List<Harbour> harbours) {
        List<HarbourDTO> harbourDTOS = new ArrayList<>();
        harbours.forEach(p -> harbourDTOS.add(new HarbourDTO(p)));
        return harbourDTOS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
