import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class TestOrder {

    private final String orderColor;
    private Customer customer;

    public TestOrder(String orderColor){
        this.orderColor = orderColor;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.baseURI;
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {"GREY BLACK"},
                {"GREY"},
                {"BLACK"},
                {""}
        };
    }

    @Test
    public void createOrder() {
        String[] arrayColor = orderColor.split(" ");
        customer = new Customer("Naruto", "Uchiha",  "Konoha, 142 apt.", "4", "+7 800 355 35 35",
                "5", "2020-06-06", "Saske, come back to Konoha",  arrayColor);
        Response response =
                (Response) given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(customer)
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
