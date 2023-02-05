import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class TestOrder {

    private final String orderColor;

    public TestOrder(String orderColor){
        this.orderColor = orderColor;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {"GREY\", \"BLACK"},
                {"GREY"},
                {"BLACK"},
                {""}
        };
    }

    @Test
    public void createOrder() {
        String json = "{\n" +
                "                \"firstName\": \"Naruto\",\n" +
                "                \"lastName\": \"Uchiha\",\n" +
                "                \"address\": \"Konoha, 142 apt.\",\n" +
                "                \"metroStation\": 4,\n" +
                "                \"phone\": \"+7 800 355 35 35\",\n" +
                "                \"rentTime\": 5,\n" +
                "                \"deliveryDate\": \"2020-06-06\",\n" +
                "                \"comment\": \"Saske, come back to Konoha\",\n" +
                "                \"color\": [\n" +
                "        \""+ orderColor +"\"\n" +
                "    ]\n" +
                "}";
        Response response =
                (Response) given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/orders");
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

    @Test
    public void orderList() {
        Response response = given()
                        .header("Content-type", "application/json")
                        .and()
                        .when()
                        .get("/api/v1/orders");
        response.then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }
}
