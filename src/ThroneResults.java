import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import static java.util.stream.Collectors.toMap;

public class ThroneResults {

    private ThroneResults() throws IOException {

        Map<String, Integer> points = new LinkedHashMap<>();
        //File input = new File("C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\20.html");
        Document doc = null;
//        URL url = new URL("https://www.game.thronemaster.net/?game=191508");
//        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//        String line = null;
//        StringBuilder tmp = new StringBuilder();
//        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
//        while ((line = in.readLine()) != null) {
//            tmp.append(line);
//        }
//
//        Document doc1 = Jsoup.parse(tmp.toString());
//        System.out.println(doc1);
        try {
            //doc = Jsoup.parse(input, "UTF-8", "");
            doc = Jsoup.connect("https://www.game.thronemaster.net/?game=191508")
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0").get();
            //System.out.println(doc);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Elements elements = Objects.requireNonNull(doc).select("div#stats_ladder");
        System.out.println(elements);
        for (Element e : elements) {
            Elements trs = e.getElementsByTag("TR");
            for (int i = 1; i < trs.size(); i++) {
                String player = trs.get(i).getElementsByTag("TD").get(1).text();
                int castles = Integer.parseInt(trs.get(i).getElementsByTag("TH").text());

                if (!points.containsKey(player)) {
                    points.put(player, castles);
                }
                else {
                    int current = points.get(player);
                    points.put(player, current + castles);
                }
            }
        }

        Map<String, Integer> sorted = points.entrySet().stream().sorted(
                Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        for (int i = 0; i < sorted.size(); i++) {
            System.out.println(String.format("%2d. %-20s %-2d", i + 1,
                    sorted.keySet().toArray()[i], sorted.values().toArray()[i]));
        }

    }

    public static void main (String[] args) throws IOException, InterruptedException, AWTException {
        //new ThroneResults();
//        OutputStream out = new FileOutputStream("C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\34.html");
//
//        URL url = new URL("https://www.game.thronemaster.net/?game=191508");
//        URLConnection conn = url.openConnection();
//        conn.connect();
//        InputStream is = conn.getInputStream();
//
//        byte[] buffer = new byte[8 * 1024];
//        int len;
//        while ((len = is.read(buffer)) > 0) {
//            out.write(buffer, 0, len);
//        }
//        is.close();
//        out.close();
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("browser.download.dir", "C:\\Users\\Admin\\Desktop\\ScreenShot\\");

        System.setProperty("webdriver.gecko.driver", "C:\\Users\\A.Monev\\Downloads\\geckodriver-v0.24.0-win64\\geckodriver.exe");
        //ProfilesIni profile = new ProfilesIni();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setAssumeUntrustedCertificateIssuer(false);
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", "localHost");
        profile.setPreference("network.proxy.http_port",3128);

        //Download setting
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperapps.neverAsk.saveToDisk","jpeg");
        profile.setPreference("browser.download.dir", "C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults");
        FirefoxOptions opt = new FirefoxOptions();
        opt.setProfile(profile);
        WebDriver driver = new FirefoxDriver(opt);
        driver.get("https://www.game.thronemaster.net/?game=191508");

        String pageSource = driver.getPageSource();
        //System.out.println(pageSource);
        Robot robot = new Robot();

        Thread.sleep(5000L);


        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(5000L);

// press Ctrl+S the Robot's way
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(5000L);

// press Enter
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

}
