package org.cloudifysource.widget.test;


import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sagib
 * Date: 06/01/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
@Test(suiteName = "users")
public class UserTest extends AbstractCloudifyWidgetTest {



    @Override
    @BeforeSuite
    public void before(){
        super.before();
        Utils.dropWidget("test");
        Utils.dropUser("test");

    }

    @Test(groups = "subscribe")
    public void subscribeTest(){
        webDriver.get(HOST + "/admin/signup");
        webDriver.findElement(By.name("email")).sendKeys(EMAIL);
        webDriver.findElement(By.name("firstname")).sendKeys("test");
        webDriver.findElement(By.name("lastname")).sendKeys("test");
        webDriver.findElement(By.name("password")).sendKeys(PASSWORD);
        webDriver.findElement(By.name("passwordConfirmation")).sendKeys(PASSWORD);
        webDriver.findElement(By.className("btn-primary")).click();
        assertUserIsLoggedIn();
    }

    @Test(dependsOnMethods = "logoutTest")
    public void loginTest(){
        webDriver.get(HOST);
        login(EMAIL, PASSWORD);
        assertUserIsLoggedIn();
    }




    private void assertUserIsLoggedIn() {
        FluentWait<By> fw = new FluentWait<By>(By.id("username"));
        fw.withTimeout(30, TimeUnit.SECONDS);
        fw.until(new Predicate<By>() {
            @Override
            public boolean apply(By input) {
                try {
                    return EMAIL.equals(webDriver.findElement(input).getText());
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }

    @Test(groups = "endSession", dependsOnMethods = "subscribeTest")
    public void logoutTest(){
        logout();
        waitForElement(By.className("login-form"));
    }





    @Test(groups = "endSession", dependsOnMethods = "loginTest")
    public void changePasswordTest(){
        webDriver.findElement(By.id("account")).click();
        waitForElement(By.id("oldPassword"));
        webDriver.findElement(By.id("oldPassword")).sendKeys(PASSWORD);
        String newPassword = PASSWORD + 2;
        webDriver.findElement(By.id("newPassword")).sendKeys(newPassword);
        webDriver.findElement(By.id("confirmPassword")).sendKeys(newPassword);
        webDriver.findElement(By.className("btn-primary")).click();
        waitForElement(By.className("alert-success"));
        logout();
        login(EMAIL, newPassword);
        assertUserIsLoggedIn();
    }
}
