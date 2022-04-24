package test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class kindergarten_crawler {
	public static String webdriver = "webdriver.chrome.driver";
	public static String path = "D:\\code\\Java\\chromedriver.exe";
	
	public static void main(String[] args) throws InterruptedException {
		run();
	}

	public static void run() throws InterruptedException {
		System.out.println("START");
		long startTime = System.currentTimeMillis();
		// 資料
		System.setProperty(webdriver, path);
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://ap.ece.moe.edu.tw/webecems/pubSearch.aspx"); //網站
		
		System.setProperty(webdriver, path);
		WebDriver googleDriver = new ChromeDriver();
		googleDriver.get("https://www.google.com.tw/maps/"); //經緯度

		WebElement searchBtn = driver.findElement(By.id("btnSearch"));
		searchBtn.click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		WebElement allPage = driver.findElement(By.id("PageControl1_lblTotalCount")); // 總筆數
		WebElement page = driver.findElement(By.id("PageControl1_lblTotalPage")); // 總頁數
		int iAllPage = Integer.valueOf(allPage.getText());
		int iPage = Integer.valueOf(page.getText());
		iPage = 2;
		// 0:園所名稱 , 1:設立別, 2:地址：縣市、鄉鎮、地址
		// GridView1_lblCity+GridView1_lblArea+GridView1_hlAddr, 3:地址的經緯度, 4:電話
		// 5:設立許可文號, 6:核准設立日期, 7:設立許可證號, 8:負責人, 9:園所網址
		// 10:核定人數, 11:全園總面積, 12:室內總面積, 13:室外活動空間總面積, 14:使用樓層
		String[] fieldArray = { "GridView1_lblSchName", "GridView1_lblPub", "GridView1_hlAddr",  "GridView1_hlAddr",
				"GridView1_lblTel", "GridView1_lblRegNum", "GridView1_lblRegDate", "GridView1_lblRegId",
				"GridView1_lblCharge", "GridView1_hlUrl", "GridView1_lblGenStd", "GridView1_lblTSpace",
				"GridView1_lblInSpace", "GridView1_lblOASpace", "GridView1_lblFloor" };
		String[] fieldNameArray = { "園所名稱", "設立別", "地址：縣市、鄉鎮、地址", "地址的經緯度",
				"電話", "設立許可文號", "核准設立日期", "設立許可證號", "負責人", "園所網址", "核定人數", "全園總面積", "室內總面積", "室外活動空間總面積", "使用樓層" };
		// 15:幼童專用車, 16:車牌號碼, 17:出廠年月，可能不只一筆
		String[] carArray = { "GridView1_lblCar", "GridView1_GridView12_@_lblCarNo", "GridView1_GridView12_@_lblMFYM" };
		String[] carNameArray = { "幼童專用車", "車牌號碼", "出廠年月" };

		String[] additionalArray = { "GridView1_lblStdPub", "GridView1_lblCoopCom", "GridView1_lblChildSvc" };
		String[] additionalNameArray = { "準公共幼兒園資格", "5歲就學補助", "兼辦小學課後" };
		WebElement element;
		By seletor;
		String city, area, addr, address = "";
		int pageCount = 10;
		for (int i = 1; i <= iPage; i++) {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			if (i == iPage) {
				pageCount = iAllPage % pageCount == 0 ? pageCount : iAllPage % pageCount;
			}

			for (int j = 0; j < pageCount; j++) {
				for (int k = 0; k < fieldArray.length; k++) {
					if (k == 2) {
						city = driver.findElement(By.id("GridView1_lblCity_" + j)).getAttribute("innerHTML");
						area = driver.findElement(By.id("GridView1_lblArea_" + j)).getAttribute("innerHTML");
						addr = driver.findElement(By.id("GridView1_hlAddr_" + j)).getAttribute("innerHTML");
						address = addr.subSequence(0, addr.indexOf("]") + 1) + city + area + addr.substring(addr.indexOf("]") + 1);
						System.out.println(fieldNameArray[k] + " : " + address);
					} else if (k == 3) {
						System.out.println(fieldNameArray[k] + " : " + getCoordinate(googleDriver, address));
					} else {
						seletor = By.id(fieldArray[k] + "_" + j);
						if (check(driver, seletor)) {
							element = driver.findElement(seletor);
							System.out.println(fieldNameArray[k] + " : " + element.getAttribute("innerHTML"));
						}
					}
				}

				seletor = By.id(carArray[0] + "_" + j);
				if (check(driver, seletor)) {
					element = driver.findElement(seletor);
					System.out.println(carNameArray[0] + " : " + element.getAttribute("innerHTML"));
				} else {
					int count = 0;
					while (true) {
						seletor = By.id(carArray[1].replace("@", String.valueOf(j)) + "_" + count);
						if (!check(driver, seletor)) {
							break;
						}
						element = driver.findElement(seletor);
						System.out.println(carNameArray[1] + " : " + element.getAttribute("innerHTML"));

						seletor = By.id(carArray[2].replace("@", String.valueOf(j)) + "_" + count);
						element = driver.findElement(seletor);
						System.out.println(carNameArray[2] + " : " + element.getAttribute("innerHTML"));
						count++;
					}
				}

				for (int k = 0; k < additionalArray.length; k++) {
					seletor = By.id(additionalArray[k] + "_" + j);
					if (check(driver, seletor)) {
						element = driver.findElement(seletor);
						System.out.println(additionalNameArray[k] + " : " + element.getAttribute("innerHTML"));
					}
				}
				System.out.println("-------------------------------------------------------------------------");
			}
			if (i != iPage) {
				element = driver.findElement(By.id("PageControl1_lbNextPage"));
				element.click();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("END");

		System.out.println("開始時間： " + startTime + "ms");
		System.out.println("結束時間： " + endTime + "ms");
		System.out.println("程式執行時間： " + (endTime - startTime) + "ms");
		System.out.println("程式執行時間： " + (endTime - startTime) / 1000 + "s");
		driver.quit(); // 關閉瀏覽器
		googleDriver.quit();
	}
 
	/**
	 * 檢視元素是否存在
	 * @param driver
	 * @param seletor
	 * @return
	 */
	public static Boolean check(WebDriver driver, By seletor) {
		try {
			driver.findElement(seletor);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * 透過Google Map取得經緯度
	 * @param driver
	 * @param address
	 * @return
	 * @throws InterruptedException
	 */
	public static String getCoordinate(WebDriver driver, String address) throws InterruptedException {
		WebElement searchTextBox = driver.findElement(By.id("searchboxinput"));
		searchTextBox.clear();
		searchTextBox.sendKeys(address);
		searchTextBox.sendKeys(Keys.RETURN);
		Thread.sleep(3000);
		WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(5));
//		WebElement addr = driver.findElement(By.id("gb_70"));
//		urlWait.until(ExpectedConditions.attributeContains(addr, "href", "!3d"));
		String coordinate = urlHandle(driver.getCurrentUrl());
		return coordinate;
	}
	
	/**
	 * 網址處理取得經緯度
	 * @param url
	 * @return
	 */
	public static String urlHandle(String url) {
		url = url.substring(url.indexOf("@"));
		String latitude, longitude;
		latitude = url.substring(1, url.indexOf(","));
		url = url.substring(url.indexOf(",") + 1);
		longitude = url.substring(0, url.indexOf(","));
		return latitude + "," + longitude;
		
	}
	
	
}
