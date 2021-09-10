import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DemoWebShopTests {

    private final SelenideElement countItemCss = $(".qty-input");


    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
        Configuration.startMaximized = true;
    }

    @Test
    void addItemShoppingCartTest() {
        open("");
        $(".header-links").$(byText("Log in")).click();
        $("#Email").setValue("testMail@yandex.ru");
        $("#Password").setValue("testUser");
        $("[value='Log in']").click();
        Cookie cookie = getWebDriver().manage().getCookieNamed("NOPCOMMERCE.AUTH");
        closeWebDriver();

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookie(cookie.toString())
                .body("product_attribute_72_5_18=65&product_attribute_72_6_19=91&product_attribute_72_3_20=58" +
                        "&product_attribute_72_8_30=94&addtocart_72.EnteredQuantity=2")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        open("/Themes/DefaultClean/Content/images/logo.png");
        getWebDriver().manage().addCookie(cookie);
        open("");
        $("#topcartlink").$(byText("Shopping cart")).click();
        $(".cart-item-row").$(".attributes")
                .shouldHave(text("Processor: Fast [+100.00] RAM: 8 GB [+60.00] " +
                        "HDD: 400 GB [+100.00] Software: Office Suite [+100.00]"));
        assertThat(countItemCss.getValue()).isEqualTo("2");

        countItemCss.clear();
        countItemCss.setValue("0").pressEnter();
        $(".order-summary-content").shouldHave(text("Your Shopping Cart is empty!"));
    }
}
