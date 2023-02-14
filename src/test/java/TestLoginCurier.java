import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class TestLoginCurier {

    private Courier courier1, courier2;

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.baseURI;
        // Создадим объекты курьеров для теста
        courier1 = new Courier("name123212316549", "pass1", "firstname1515");
        courier2 = new Courier("name28528525154252", "pass2", "firstname4321");
    }

    @Test
    public void testCourierCanLogin() {
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
    }

    @Test
    public void testCourierWrongUserLogin() {

        // 2. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier2)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    public void testCourierOnlyPasswordLogin() {
        // Создадим курьера для выполнения теста
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        // 3. Ошибка если только пароль
        String saveField = courier1.getLogin();
        courier1.setLogin("");
        Response response = given()
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
    }

    @Test
    public void testCourierOnlyLoginLogin() {
        // Создадим курьера для выполнения теста
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        // 4. Ошибка если только логин
        String saveField = courier1.getPassword();
        courier1.setPassword("");
        Response response = given()
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
    }

    @Test
    public void testCourierOnlyWrongLoginLogin() {
        // Создадим курьера для выполнения теста
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        // 5. Ошибка если неправильный логин
        String saveField = courier1.getLogin();
        courier1.setLogin("wronglogin");
        Response response = given()
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
    }

    @Test
    public void testCourierWrongPasswordLogin() {
        // Создадим курьера для выполнения теста
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier1)
                .when()
                .post("/api/v1/courier");
        // 6. Ошибка если неправильный пароль
        String saveField = courier1.getPassword();
        courier1.setPassword("wrongpassword");
        Response response = given()
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
