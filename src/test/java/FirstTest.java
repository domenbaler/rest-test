import org.testng.annotations.Test;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class FirstTest{

    @Test
    public void test_get() {
        get("http://localhost:8080/rest").
        then().
        assertThat().
            statusCode(200).
        and().
            contentType(ContentType.JSON).
        and().
            body("NumberOfSuccessfulCalls",equalTo("0")).
        and().
            body("NumberOfUnsuccessfulCalls", equalTo("0"));
    }

    @Test(dependsOnMethods="test_get")
    public void test_valid_param() {
        post("http://localhost:8080/rest/4").
        then().
        assertThat().
        statusCode(204);
    }

    @Test
    public void test_invalid_param() {
        post("http://localhost:8080/rest/5").
        then().
        assertThat().
        statusCode(400);
    }

    @Test(dependsOnMethods="test_valid_param")
    public void test_get_after_post() throws InterruptedException {
        Thread.sleep(5000);
        get("http://localhost:8080/rest").
        then().
        assertThat().
            statusCode(200).
        and().
            contentType(ContentType.JSON).
        and().
            body("NumberOfSuccessfulCalls",equalTo("4")).
        and().
            body("NumberOfUnsuccessfulCalls", equalTo("0")).
        and().body(containsString("Kariera – RESULT"));
    }

}
