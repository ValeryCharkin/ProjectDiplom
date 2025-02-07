package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.PaymentTypesPage;

import static com.codeborne.selenide.Selenide.open;

public class CreditCardTest {

    String approvedCardNumber = DataHelper.getCardApproved().getCardNumber();
    String declinedCardNumber = DataHelper.getCardDeclined().getCardNumber();
    String validMonth = DataHelper.getRandomMonth(1);
    String validYear = DataHelper.getRandomYear(1);
    String validOwnerName = DataHelper.getRandomName();
    String validCode = DataHelper.getNumberCVC(3);

    @BeforeEach
    public void setUp() {
        open("http://localhost:8080");
    }

    @AfterEach
    public void shouldCleanBase() {
        SQLHelper.сleanBase();
    }

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    public void shouldCreditPaymentApproved() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.bankApprovedOperation();
        Assertions.assertEquals("APPROVED", SQLHelper.getCreditPayment());
    }

    @Test
    public void shouldDeclinedCardPayment() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(declinedCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.bankDeclinedOperation();
        Assertions.assertEquals("Declined", SQLHelper.getCreditPayment());
    }

    @Test
    public void shouldHandleInvalidCardDetails() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();

        // Test invalid card number
        var invalidCardNumber = DataHelper.GetAShortNumber();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(invalidCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();

        // Test empty card number
        var emptyCardNumber = DataHelper.getEmptyField();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(emptyCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();

        // Test expired card month
        var monthExpired = DataHelper.getRandomMonth(-2);
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, monthExpired, validYear, validOwnerName, validCode);
        creditCardPage.errorCardTermValidity();

        // Test expired card year
        var expiredYear = DataHelper.getRandomYear(-5);
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, expiredYear, validOwnerName, validCode);
        creditCardPage.termValidityExpired();
    }

    @Test
    public void shouldHandleInvalidOwnerName() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();

        // Test Russian name
        var rusLanguageName = DataHelper.getRandomNameRus();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, rusLanguageName, validCode);
        creditCardPage.errorFormat();

        // Test digits in name
        var digitsName = DataHelper.getNumberName();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, digitsName, validCode);
        creditCardPage.errorFormat();

        // Test special symbols in name
        var specSymbolsName = DataHelper.getSpecialCharactersName();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, specSymbolsName, validCode);
        creditCardPage.errorFormat();

        // Test empty name
        var emptyName = DataHelper.getEmptyField();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, emptyName, validCode);
        creditCardPage.emptyField();
    }

    @Test
    public void shouldHandleInvalidCVC() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();

        // Test two-digit CVC
        var twoDigitCVC = DataHelper.getNumberCVC(2);
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, twoDigitCVC);
        creditCardPage.errorFormat();

        // Test one-digit CVC
        var oneDigitCVC = DataHelper.getNumberCVC(1);
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, oneDigitCVC);
        creditCardPage.errorFormat();

        // Test empty CVC
        var emptyCVC = DataHelper.getEmptyField();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, emptyCVC);
        creditCardPage.errorFormat();

        // Test special symbols in CVC
        var specSymbolsCVC = DataHelper.getSpecialCharactersName();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, specSymbolsCVC);
        creditCardPage.errorFormat();
    }

    @Test
    public void shouldHandleAllFieldsEmpty() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyField = DataHelper.getEmptyField();
        creditCardPage.cleanFields();
        creditCardPage.fillCardPaymentForm(emptyField, emptyField, emptyField, emptyField, emptyField);
        creditCardPage.errorFormat();
    }
}