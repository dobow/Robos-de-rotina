package stepAdjuster;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
	private static WebDriver driver = null;
	static String login = "";
	static String password = "";

	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("./configs/config.properties");
		props.load(file);
		return props;

	}

	public static void main(String[] args) throws InterruptedException {
		Properties prop;
		try {
			prop = getProp();
			login = prop.getProperty("app.login");
			password = prop.getProperty("app.senha");
		} catch (IOException e2) {
		}
		System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
		startDriver();
		driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 60);
		WebDriverWait waitf = new WebDriverWait(driver, 5);
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id<locator>));
//		String baseUrl = "https://app.x-celera.com/xcelera-app/secure/execution-plans/2822/manage-execution";
		String baseUrl = "https://app.x-celera.com/xcelera-app/secure/execution-plans/2814/manage-execution";

		// telaLogin
		String txtUser = "//*[@id=\"Username\"]";
		String txtPass = "//*[@id=\"Password\"]";
		String btnLogin = "//button[.='Login']";
		String cbbItemPatrimonial = "//*[.,'SQUAD - Patrimonial']"; // 29
		String optPatr = "//option[@value='29']";
		String btnAcessProject = "//button[.='Access Project ']";

		driver.get(baseUrl);
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(txtUser))).sendKeys(login);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(txtPass))).sendKeys(password);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnLogin))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select"))).click();
		esperar(1);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(optPatr))).click();
		esperar(1);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(txtPass))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnAcessProject))).click();
		esperar(2);
		driver.get(baseUrl);

		String lblFailed = "//td[.='Failed']/..";
//		String lblFailed = "//tr[contains(@class,'text text-danger')]/td";
		String btnCancel = "//button[.='Cancel']";
		String btnSave = "(//button[.='Save'])[2]";
//		String btnEditAction = "//td[.='Failed']/../td[5]/button[1][@title='View Action']";
		String btnEditAction = "/..//td[5]/button[1][@title='View Action']";
		String btnRerun = "/..//td[5]/button[3][@title='Rerun Test Case']";
		String mdlEditarTestCase = "//mat-dialog-container";
		String btnEditarValorTDM = "//label[.='Selected Actions']/..//span[.='Preencher Dados do Local de Risco']/../../div[2]/div[2]/button[1]";
//		String lbl = "//span[.='TXT_Numero_Local']/../..";
		String txtValorNumeroLocal = "//span[.='TXT_Numero_Local']/ancestor::table/tbody/tr/td[1]";
		String listaDeStrings = "//tr[contains(@class,'text text-danger')]/td[1]";
//		String lbl = "";

		List<String> currentList = new ArrayList<String>();
		List<String> totalList = new ArrayList<String>();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(lblFailed)));
		esperar(5);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@class='btn-group btn-group-toggle']/label[contains(.,'Failed')]"))).click();
//		List<WebElement> tRows = driver.findElements(By.xpath(lblFailed));
		List<WebElement> ownElements = driver.findElements(By.xpath(listaDeStrings));
		int loop = 0;
		while (loop++ < 100) {
			currentList.clear();
			esperar(6);
			ownElements.forEach(node -> {
				currentList.add(node.getAttribute("innerHTML").trim());
			});
			System.out.println("total: " + totalList.size());
			try {
				for (String webElement : currentList) {
					if (totalList.contains(webElement)) {
						System.out.println("contem: " + webElement);
					} else {
						System.out.println(webElement);
						try {
							String xpathExpression = "//td[contains(@title,'" + webElement.replace("...", "") + "')]"
									+ btnEditAction;
							driver.findElement(By.xpath(xpathExpression)).click();
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(mdlEditarTestCase)));
							waitf.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnEditarValorTDM)));
							List<WebElement> clic = driver.findElements(By.xpath(btnEditarValorTDM));
							System.out.println("N Elements " + clic.size());
							for (WebElement webElement2 : clic) {
								webElement2.click();
								definirValor(waitf, btnSave, txtValorNumeroLocal);
							}
						} catch (TimeoutException ignore) {
						} catch (Exception e) {
						}

//					esperar(5);

						// cancelar e ir pro proximo
						List<WebElement> lBtnCancel = driver.findElements(By.xpath(btnCancel));
						try {
							while (lBtnCancel.size() > 0) {
								try {
									waitf.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnCancel)));
									lBtnCancel.forEach(e -> {
										e.click();
										try {
											Thread.sleep(350);
										} catch (InterruptedException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									});
								} catch (Exception e) {
									lBtnCancel.get(lBtnCancel.size() - 1).click();
									lBtnCancel = driver.findElements(By.xpath(btnCancel));
								}
							}
						} catch (Exception e) {
						}
//					JavascriptExecutor js = (JavascriptExecutor) driver;
//					js.executeScript("arguments[0].remove();", webElement);
						String xpathExpression = "//td[contains(@title,'" + webElement.replace("...", "") + "')]"
								+ btnRerun;
						driver.findElement(By.xpath(xpathExpression)).click();
						esperar(5);
					}
				}
			} catch (StaleElementReferenceException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
//			waitf.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[.='Next']"))).click();
			esperar(1);
			totalList.addAll(currentList);
			ownElements = driver.findElements(By.xpath(listaDeStrings));
		}

		esperar(20);
		driver.close();

	}

	private static void definirValor(WebDriverWait waitf, String btnSave, String txtValorNumeroLocal)
			throws AWTException {
		WebElement elementa = waitf.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(txtValorNumeroLocal)));

//							if (elementa.getText().contains("<IGNORE>")) {
		esperar(1);
		Actions action = new Actions(driver);
		action.doubleClick(elementa).perform();

		Robot robot = new Robot();
		try {
			robot.keyPress(KeyEvent.VK_CONTROL);
			Thread.sleep(100);
			robot.keyPress(KeyEvent.VK_A);
			Thread.sleep(200);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(100);
			robot.keyRelease(KeyEvent.VK_A);
			Thread.sleep(200);
			robot.keyPress(KeyEvent.VK_DELETE);
			robot.keyRelease(KeyEvent.VK_DELETE);
			Thread.sleep(100);
			robot.keyPress(KeyEvent.VK_1);
			robot.keyRelease(KeyEvent.VK_1);
			Thread.sleep(100);
			robot.keyPress(KeyEvent.VK_1);
			robot.keyRelease(KeyEvent.VK_1);
			Thread.sleep(100);
			robot.keyPress(KeyEvent.VK_1);
			robot.keyRelease(KeyEvent.VK_1);
			Thread.sleep(200);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception e) {
		}
		esperar(1);
		waitf.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnSave))).click();
		esperar(2);
//							}
	}

	private static void esperar(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (Exception ignorar) {
		}
	}

	private static void startDriver() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "DriverSelenium"
				+ File.separator + "chromedriver.exe");
		System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		Logger.getLogger("org.slf4j.impl.StaticLoggerBinder").setLevel(Level.OFF);
		ChromeOptions chromeOptions = new ChromeOptions();
		// chromeOptions.addArguments("--user-data-dir=C:/Users/grupohdi01/AppData/Local/Google/Chrome/User
		// Data");
		// chromeOptions.addArguments("--profile-directory=Default");
		chromeOptions.addArguments("--lang=pt");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--disable-web-security");
		chromeOptions.addArguments("disable-infobars");
		chromeOptions.addArguments("--window-size=1920,1080");
		chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
//		chromeOptions.addExtensions(new File(System.getProperty("user.dir") + File.separator + "Extensions"
//				+ File.separator + "extension_2_3_164_0.crx"));

		driver = new ChromeDriver(chromeOptions);
		driver.manage().deleteAllCookies();
	}

}
