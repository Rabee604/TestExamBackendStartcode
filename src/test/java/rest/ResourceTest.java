package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.BoatDTO;
import dto.HarbourDTO;
import dto.OwnerDTO;
import entities.*;
import facades.Facade;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class ResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static Owner owner1,owner2,owner3;
    private static Harbour harbour1, harbour2;
    private static Boat boat1,boat2,boat3;
    private static OwnerDTO owner1DTO,owner2DTO,owner3DTO;
    private static BoatDTO boat1DTO,boat2DTO,boat3DTO;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        EntityManager em = emf.createEntityManager();


        User user = new User("user", "test123");
        User admin = new User("admin", "test123");
        User both = new User("user_admin", "test123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        boat1 = new Boat("Brand1","Make1","Boat1","ImageURL1");
        boat2 = new Boat("Brand2","Make2","Boat2","ImageURL2");
        boat3 = new Boat("Brand3","Make3","Boat3","ImageURL3");

        harbour1 = new Harbour("Harbour1","Address1","20");
        harbour2 = new Harbour("Harbour2","Address2","15");

        owner1 = new Owner("Owner1","HomeAddress1","12345678");
        owner2 = new Owner("Owner2","HomeAddress2","87654321");
        owner3 = new Owner("Owner3","HomeAddress3","43215678");


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

        try{
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
        owner1DTO= new OwnerDTO(owner1);
        owner2DTO= new OwnerDTO(owner2);
        owner3DTO= new OwnerDTO(owner3);
        boat1DTO= new BoatDTO(boat1);
        boat2DTO= new BoatDTO(boat2);
        boat3DTO= new BoatDTO(boat3);

    }

    @AfterEach
    void tearDown() {
        emf.close();
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/info").then().statusCode(200);
    }

    //This test assumes the database contains two rows

    @Test
    public void getAllOwners() {
        System.out.println("Testing all owners");

        List<OwnerDTO> actualOwnerListDTO = given()
                .contentType("application/json")
                .when()
                .get("/info/owners")
                .then()
                .extract().body().jsonPath().getList("",OwnerDTO.class);

        assertThat(actualOwnerListDTO, containsInAnyOrder(owner1DTO,owner2DTO,owner3DTO));
    }
    @Test
    void getOwnerByBoat() {
        System.out.println("Testing to get getOwnerByBoat");
        List<OwnerDTO> actualPersonsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/info/boat/" + boat1.getName())
                .then()
                .extract().body().jsonPath().getList("", OwnerDTO.class);
        assertThat(actualPersonsDTOs, containsInAnyOrder( owner1DTO, owner3DTO));
    }
    @Test
    void getBoatByHarbour() {
        System.out.println("Testing to get getBoatByHarbour");
        List<BoatDTO> actualBoatDTOs = given()
                .contentType("application/json")
                .when()
                .get("/info/harbour/"+ harbour1.getName())
                .then()
                .extract().body().jsonPath().getList("", BoatDTO.class);
        assertThat(actualBoatDTOs, containsInAnyOrder(boat2DTO,boat1DTO));
    }
    @Test
    void createBoat() {
        System.out.println("Testing to create a boat");

        Facade facade = Facade.getFacade(emf);
        Boat boat = new Boat("Brand4","Make4","Boat4","ImageURL4");
        BoatDTO actualBoatDTO = new BoatDTO(boat);
        String s = GSON.toJson(actualBoatDTO);

        BoatDTO expectedBoatDTO = given()
                .contentType("application/json").body(s)
                .when()
                .post("/info/create")
                .then()
                .extract().body().jsonPath().getObject("", BoatDTO.class);

        assertThat(actualBoatDTO.getName(), equalTo(expectedBoatDTO.getName()));
    }

    @Test
    void deleteBoatById()
    {
        System.out.println("Testing to delete boat by id");
        BoatDTO boatDTO = given()
                .contentType("application/json")
                .when()
                .delete("/info/delete/" +  boat3DTO.getId())
                .then()
                .extract().body().jsonPath().getObject("", BoatDTO.class);

        assertThat(boatDTO.getName(), equalTo(new BoatDTO(boat3).getName()));
    }

   /* @Test
   public void setBoatHarbour() {
        System.out.println("Testing to set the boat to harbour");

        String body = "{'boatName':'Boat4','harbourName':'Harbour2'}";
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(body);
           List <HarbourDTO> actualBoatDTOs = given()
                .contentType("application/json").body(jsonObject)
                .when()
                .post("/info/setharbour")
                .then()
                .extract().body().jsonPath().getObject("",HarbourDTO.class);

        assertThat(, containsInAnyOrder();
    }*/


}

