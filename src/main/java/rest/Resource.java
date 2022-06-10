package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.BoatDTO;
import dto.HarbourDTO;
import dto.OwnerDTO;
import entities.Boat;
import entities.Harbour;
import entities.User;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import errorhandling.API_Exception;
import facades.Facade;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class Resource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Facade facade = Facade.getFacade(EMF_Creator.createEntityManagerFactory());
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @GET
    @Path("/owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomer() {
        List<OwnerDTO> c = facade.getAllOwners();
        return Response
                .ok()
                .entity(gson.toJson(c))
                .build();

    }
    @GET
    @Path("/harbour/{harbour}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getBoatByharbour(@PathParam("harbour") String harbour) {
        return Response
                .ok()
                .entity(gson.toJson(facade.getBoatsByHarbour(harbour)))
                .build();
    }
    @GET
    @Path("/boat/{boat}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOwnerByBoat(@PathParam("boat") String boat) {
        return Response
                .ok()
                .entity(gson.toJson(facade.getOwnersByBoat(boat)))
                .build();
    }

    @POST
    @Path("create")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createBoat(String content) {
        BoatDTO boatDTO = gson.fromJson(content, BoatDTO.class);
        return Response
                .ok()
                .entity(gson.toJson(facade.create(boatDTO)))
                .build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("setharbour")
    public Response setBoatHarbour(String jsonString)throws API_Exception {

        EntityManager em = EMF.createEntityManager();

        String boatName;
        String harbourName;
        try{
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            boatName = json.get("boatName").getAsString();
            harbourName = json.get("harbourName").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
                return Response
                .ok()
                .entity(gson.toJson(facade.setBoatHarbour(boatName,harbourName)))
                .build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response deletePersonById(@PathParam("id") Long id)
    {
        BoatDTO boatDTO = facade.deleteBoatByID(id);
        return Response
                .ok()
                .entity(gson.toJson(boatDTO))
                .build();
    }

}