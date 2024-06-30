import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.SendKeysAction;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainTest {

        public static AndroidDriver<MobileElement> driver;
        private static String appBundle = "org.joinmastodon.android";

        @BeforeTest
        public void setUp() throws MalformedURLException {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "android");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13.0");
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "uiautomator2");
            capabilities.setCapability(MobileCapabilityType.UDID, "34HDU20305002432");

            capabilities.setCapability("appPackage", appBundle);
            capabilities.setCapability("appActivity", appBundle+".MainActivity");

            URL appiumUrl = new URL("http://127.0.0.1:4723");
            driver = new AndroidDriver<>(appiumUrl, capabilities);
        }

        @Test
        public void checkStartupShutdownSearchingElements() throws InterruptedException {
            Utils.logStep("Open Mastodon and make sure that welcome screen is opened");
            MobileElement welcomeScreenLbl = driver.findElement(By.id("org.joinmastodon.android:id/art_container"));
            Assert.assertTrue(welcomeScreenLbl.isDisplayed());

            Utils.logStep("Close Mastodon without closing the session");
            driver.terminateApp(appBundle);

            Utils.logStep("Open mastodon");
            driver.activateApp(appBundle);
            welcomeScreenLbl = driver.findElement(By.id("org.joinmastodon.android:id/art_container"));
            Assert.assertTrue(welcomeScreenLbl.isDisplayed());

            Utils.logStep("Log in the app (your account will be saved, so you just need to enter the server " +
                    "name and tap \"Authorize\") and make sure, that home screen is opened");
            authorizeApp();

            Utils.logStep("Tap 'Explore' Tab and make sure that 'Posts' screen is opened");
            driver.findElement(By.id("tab_search")).click();
            MobileElement postsLbl = driver.findElement(By.id("discover_content"));
            Assert.assertTrue(postsLbl.isDisplayed());

            Utils.logStep("Open the first post and check that it's opened");
            MobileElement firstPostCardLbl = driver.findElement(By.id("text"));
            String firstPostCardText = firstPostCardLbl.getAttribute("text");
            firstPostCardLbl.click();
            MobileElement firstPostLbl = driver.findElement(By.id("text"));
            String firstPostText = firstPostLbl.getAttribute("text");
            Assert.assertEquals(firstPostCardText, firstPostText);

            Utils.logStep("Close the app and the session");
            driver.closeApp();
        }

    @Test
    public void checkInteractionWithElements() throws InterruptedException {
            Utils.logStep("Open Mastodon and make sure that welcome screen is opened");
            MobileElement welcomeScreenLbl = driver.findElement(By.id("art_container"));
            Assert.assertTrue(welcomeScreenLbl.isDisplayed());

            Utils.logStep("Log in the app (your account will be saved, so you just need to enter the server " +
                    "name and tap \"Authorize\") and make sure, that home screen is opened");
            authorizeApp();

            Utils.logStep("Tap 'Explore' Tab and make sure that 'Posts' screen is opened");
            driver.findElement(By.id("tab_search")).click();
            MobileElement postsLbl = driver.findElement(By.id("discover_content"));
            Assert.assertTrue(postsLbl.isDisplayed());

            Utils.logStep("Check that posts are in the Displayed state");
            MobileElement postLbl = driver.findElement(By.id("text"));
            Assert.assertTrue(postLbl.isDisplayed());

            Utils.logStep("Get the position of a search field and make sure that Search field doesn't" +
                    " have the 0:0 position value");
            MobileElement searchPostTxb = driver.findElement(By.id("search_text"));
            Point searchLoc = searchPostTxb.getLocation();
            Assert.assertTrue(searchLoc.getX()!=0);
            Assert.assertTrue(searchLoc.getY()!=0);

            Utils.logStep("Enter 'tests' and make sure that Search field contains 'tests'");
            searchPostTxb.click();
            MobileElement searchText = driver.findElement(By.className("android.widget.EditText"));
            searchText.sendKeys("tests");
            Assert.assertEquals(searchText.getAttribute("text"), "tests");

            Utils.logStep("Clear the search field and make sure that Search field contains 'Search Mastodon'");
            searchText.clear();
            Assert.assertEquals(searchText.getAttribute("text"), "Search Mastodon");
    }

    @Test
    public void checkKeyboardProcessing() throws InterruptedException {
        Utils.logStep("Open Mastodon and make sure that welcome screen is opened");
        MobileElement welcomeScreenLbl = driver.findElement(By.id("art_container"));
        Assert.assertTrue(welcomeScreenLbl.isDisplayed());

        Utils.logStep("Log in the app (your account will be saved, so you just need to enter the server " +
                "name and tap \"Authorize\") and make sure, that home screen is opened");
        authorizeApp();

        Utils.logStep("Tap 'Explore' Tab and make sure that 'Posts' screen is opened");
        driver.findElement(By.id("tab_search")).click();
        MobileElement postsLbl = driver.findElement(By.id("discover_content"));
        Assert.assertTrue(postsLbl.isDisplayed());

        Utils.logStep("Tap the search field. We need the virtual keyboard to appear");
        tapSearch();

        Utils.logStep("Try closing it using the \"Search\" button on the virtual keyboard and log the results (if it is closed or not)");
        driver.pressKey(new KeyEvent(AndroidKey.SEARCH));
        System.out.println("Keyboard is shown: "+driver.isKeyboardShown());

        Utils.logStep("If virtual keyboard is closed tap the search field. We need the virtual keyboard to appear");
        tapSearchIfKeywordHidden();

        Utils.logStep("Try closing it using the \"SendKeys()\" method and log the results (if it is closed or not)");
        MobileElement searchText = driver.findElement(By.className("android.widget.EditText"));
        searchText.sendKeys("text");
        System.out.println("Keyboard is shown: "+driver.isKeyboardShown());

        Utils.logStep("If virtual keyboard is closed tap the search field. We need the virtual keyboard to appear");
        tapSearchIfKeywordHidden();

        Utils.logStep("Try closing it using the HideKeyboard() / hideKeyboard() method and log the results (if it is closed or not)");
        driver.hideKeyboard();
        System.out.println("Keyboard is shown: "+driver.isKeyboardShown());

        Utils.logStep("If virtual keyboard is closed tap the search field. We need the virtual keyboard to appear");
        searchText.click();

        Utils.logStep("Enter some characters using keyboard interaction methods (e.g. pressKey())");
        driver.pressKey(new KeyEvent(AndroidKey.A));


    }

    private void authorizeApp() throws InterruptedException {
        driver.findElement(By.id("btn_log_in")).click();
        driver.findElement(By.id("search_edit")).sendKeys("mastodon.social");
        driver.findElement(By.id("radiobtn")).click();
        driver.findElement(By.id("btn_next")).click();
        driver.findElement(By.xpath("//android.widget.Button[@text=\"Авторизовать\"]")).click();
        Thread.sleep(3000); //here is supposed to be wait until condition
        MobileElement homeScreenLbl = driver.findElement(By.id("toolbar"));
        Assert.assertTrue(homeScreenLbl.isDisplayed());
    }

    private void tapSearchIfKeywordHidden(){
        if (!driver.isKeyboardShown()){
            tapSearch();
        };
    }

    private void tapSearch(){
        MobileElement searchPostTxb = driver.findElement(By.id("search_text"));
        searchPostTxb.click();
    }

     @AfterTest
    public void tearDown(){
        driver.quit();
    }
}