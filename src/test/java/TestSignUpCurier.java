import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class TestSignUpCurier {

    private Courier courier1, courier2;

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.baseURI;
        // Создадим объекты курьеров для теста
        courier1 = new Courier("name123212316549", "pass1", "firstname1515");
        courier2 = new Courier("name28528525154252", "pass2", "firstname4321");
    }

    @Test
    public void testCreateCourier() {
        // 1. курьера можно создать
        Response response =
                (Response) given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier1)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    public void testCreateDoubleCourier() {
        // 2. нельзя создать двух одинаковых курьеров
        given().header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @Test
    public void testCreateOnlyFullDataCourier() {
        // 3. чтобы создать курьера, нужно передать в ручку все обязательные поля
        courier2.setLogin("");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier2)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
        courier2.setLogin("TrueLogin568357638456");
        courier2.setPassword("");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier2)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @After
    public void deleteAllData() {
        Integer courierId = given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier1)
                        .when()
                        .post("/api/v1/courier/login")
                        .then().extract().body().path("id");
        System.out.println("Тестовые данные удалены");
        try {
            given().header("Content-type", "application/json")
                    .and()
                    .when()
                    .delete("/api/v1/courier/" + courierId.toString())
                    .then().statusCode(200);
        } catch (NullPointerException exception) {
            System.out.println("Удаляемый курьер не существует");
        }
    }
}
