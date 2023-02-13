import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class TestCurier {

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
        // 2. нельзя создать двух одинаковых курьеров
        response = given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier1)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
        // 3. чтобы создать курьера, нужно передать в ручку все обязательные поля
        courier2.setLogin("");
        response = given()
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

    @Test
    public void loginCourier() {
        // Создадим курьера для выполнения теста
        given()
            .header("Content-type", "application/json")
            .and()
            .body(courier1)
            .when()
            .post("/api/v1/courier");
        // 1. курьер может авторизоваться
        Response response = given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier1)
                        .when()
                        .post("/api/v1/courier/login");
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
        // 2. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку
        response = given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier2)
                        .when()
                        .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
        // 3. Ошибка если только пароль
        String saveField = courier1.getLogin();
        courier1.setLogin("");
        response = given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier1)
                        .when()
                        .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
        // Вернем поле на место
        courier1.setLogin(saveField);
        // 4. Ошибка если только логин
        saveField = courier1.getPassword();
        courier1.setPassword("");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
        // Вернем поле на место
        courier1.setPassword(saveField);
        // 5. Ошибка если неправильный логин
        saveField = courier1.getLogin();
        courier1.setLogin("wronglogin");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
        // Вернем поле на место
        courier1.setLogin(saveField);
        // 4. Ошибка если неправильный пароль
        saveField = courier1.getPassword();
        courier1.setPassword("wrongpassword");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
        // Вернем поле на место
        courier1.setPassword(saveField);

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
