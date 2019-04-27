import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class ThroneResults {
    private Map<String, Integer> POINTS = new LinkedHashMap<>();
    private Map<String, Double> MINUTES = new LinkedHashMap<>();
    private Map<String, Integer> MOVES = new LinkedHashMap<>();
    private Map<String, Double> SPEED = new LinkedHashMap<>();
    private int[] ROUNDS = new int[11];

    private ThroneResults() throws AWTException, InterruptedException {

        getFiles();

        for (int game = 1; game < 25; game++) {
            process(game);
        }

        Map<String, Integer> sortedPoints = POINTS.entrySet().stream().sorted(
                Collections.reverseOrder(comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        System.out.println("Standings");
        for (int i = 0; i < sortedPoints.size(); i++) {
            System.out.println(String.format("%2d. %-20s %-2d", i + 1,
                    sortedPoints.keySet().toArray()[i], sortedPoints.values().toArray()[i]));
        }

        for (int i = 0; i < MINUTES.size(); i++) {
            MINUTES.forEach((player, minutes) ->
                    SPEED.put(player, minutes / MOVES.get(player)));
        }

        Map<String, Double> sortedSpeed = SPEED.entrySet().stream().sorted(comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        System.out.println();
        System.out.println("Top Speedsters");
        for (int i = 0; i < sortedSpeed.size(); i++) {
            System.out.println(String.format("%2d. %-20s %.0f mpm", i + 1,
                    sortedSpeed.keySet().toArray()[i], sortedSpeed.values().toArray()[i]));
        }

        System.out.println();
        System.out.println("Games per rounds");
        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("%2d - %-2s", i + 1, ROUNDS[i]));
        }
        System.out.println(String.format("Finished - %-2s", ROUNDS[10]));

    }

    private void process(int game) {
        File input = new File("C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\games\\" + game + ".html");
        Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8", "");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Elements state = Objects.requireNonNull(doc).select("div#gameStateText");
        boolean finished = !state.get(0).getElementsByTag("h3").isEmpty();
        int bonus = finished ? 3 : 0;

        Elements statsLadder = Objects.requireNonNull(doc).select("div#stats_ladder");
        for (Element e : statsLadder) {
            Elements trs = e.getElementsByTag("TR");
            for (int i = 1; i < trs.size(); i++) {
                String player = trs.get(i).getElementsByTag("TD").get(1).text();
                int castles = Integer.parseInt(trs.get(i).getElementsByTag("TH").text());
                if (i == 1) {
                    castles += bonus;
                }

                if (!POINTS.containsKey(player)) {
                    POINTS.put(player, castles);
                }
                else {
                    int current = POINTS.get(player);
                    POINTS.put(player, current + castles);
                }
            }
        }

        Elements statsSpeed = Objects.requireNonNull(doc).select("div#stats_speed");
        for (Element e : statsSpeed) {
            Elements trs = e.getElementsByTag("TR");
            for (int i = 0; i < trs.size() - 1; i++) {
                String player = trs.get(i).getElementsByTag("TD").get(1).text();
                String text = trs.get(i).getElementsByTag("TD").get(2).text();
                int m = text.indexOf("m");
                double speed = Double.parseDouble(text.substring(0, m - 1));
                int moves = Integer.parseInt(text.substring(m + 6));

                if (!MINUTES.containsKey(player)) {
                    MINUTES.put(player, speed * moves);
                    MOVES.put(player, moves);
                }
                else {
                    MINUTES.put(player, MINUTES.get(player) + speed * moves);
                    MOVES.put(player, MOVES.get(player) + moves);
                }
            }
        }

        if (finished) {
            ROUNDS[10]++;
        }
        else {
            Elements gameStateContent = Objects.requireNonNull(doc).select("div#gameStateContent");
            int round = Integer.parseInt(gameStateContent.first().child(0).child(0).text());
            ROUNDS[round - 1]++;
        }
    }

    private void getFiles() throws InterruptedException, AWTException {
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\A.Monev\\Downloads\\geckodriver-v0.24.0-win64\\geckodriver.exe");
        FirefoxProfile profile = new FirefoxProfile();
        profile.setAssumeUntrustedCertificateIssuer(false);
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", "localHost");
        profile.setPreference("network.proxy.http_port",3128);

        //Download setting
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperapps.neverAsk.saveToDisk","jpeg");
        profile.setPreference("browser.download.dir", "C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\games");
        FirefoxOptions opt = new FirefoxOptions();
        opt.setProfile(profile);

        saveAs(opt, 1, "https://www.game.thronemaster.net/?game=191498");
        saveAs(opt, 2, "https://www.game.thronemaster.net/?game=191494");
        saveAs(opt, 3, "https://www.game.thronemaster.net/?game=191516");
        saveAs(opt, 4, "https://www.game.thronemaster.net/?game=191476");
        saveAs(opt, 5, "https://www.game.thronemaster.net/?game=191499");
        saveAs(opt, 6, "https://www.game.thronemaster.net/?game=191550");
        saveAs(opt, 7, "https://www.game.thronemaster.net/?game=191514");
        saveAs(opt, 8, "https://www.game.thronemaster.net/?game=191512");
        saveAs(opt, 9, "https://www.game.thronemaster.net/?game=191493");
        saveAs(opt, 10, "https://www.game.thronemaster.net/?game=191477");
        saveAs(opt, 11, "https://www.game.thronemaster.net/?game=191519");
        saveAs(opt, 12, "https://www.game.thronemaster.net/?game=191478");
        saveAs(opt, 13, "https://www.game.thronemaster.net/?game=191495");
        saveAs(opt, 14, "https://www.game.thronemaster.net/?game=191515");
        saveAs(opt, 15, "https://www.game.thronemaster.net/?game=191517");
        saveAs(opt, 16, "https://www.game.thronemaster.net/?game=191500");
        saveAs(opt, 17, "https://www.game.thronemaster.net/?game=191509");
        saveAs(opt, 18, "https://www.game.thronemaster.net/?game=191496");
        saveAs(opt, 19, "https://www.game.thronemaster.net/?game=191479");
        saveAs(opt, 20, "https://www.game.thronemaster.net/?game=191508");
        saveAs(opt, 21, "https://www.game.thronemaster.net/?game=191507");
        saveAs(opt, 22, "https://www.game.thronemaster.net/?game=191480");
        saveAs(opt, 23, "https://www.game.thronemaster.net/?game=191481");
        saveAs(opt, 24, "https://www.game.thronemaster.net/?game=191497");
    }

    private void saveAs(FirefoxOptions opt, int number, String link)
            throws AWTException, InterruptedException {

        WebDriver driver = new FirefoxDriver(opt);
        driver.get(link);
        Robot robot = new Robot();

        Thread.sleep(3000L);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(3000L);

        // press Ctrl+S the Robot's way
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(2000L);

        if (number > 9) {
            robot.keyPress(getKeyEvent(number / 10));
            robot.keyRelease(getKeyEvent(number / 10));
        }
        robot.keyPress(getKeyEvent(number % 10));
        robot.keyRelease(getKeyEvent(number % 10));
        robot.keyPress(KeyEvent.VK_PERIOD);
        robot.keyRelease(KeyEvent.VK_PERIOD);
        robot.keyPress(KeyEvent.VK_H);
        robot.keyRelease(KeyEvent.VK_H);
        robot.keyPress(KeyEvent.VK_T);
        robot.keyRelease(KeyEvent.VK_T);
        robot.keyPress(KeyEvent.VK_M);
        robot.keyRelease(KeyEvent.VK_M);
        robot.keyPress(KeyEvent.VK_L);
        robot.keyRelease(KeyEvent.VK_L);

        Thread.sleep(1000L);

        // press Enter
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(2000L);

        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(5000L);

        driver.close();
    }

    private int getKeyEvent(int digit) {
        switch (digit) {
            case 0: return KeyEvent.VK_0;
            case 1: return KeyEvent.VK_1;
            case 2: return KeyEvent.VK_2;
            case 3: return KeyEvent.VK_3;
            case 4: return KeyEvent.VK_4;
            case 5: return KeyEvent.VK_5;
            case 6: return KeyEvent.VK_6;
            case 7: return KeyEvent.VK_7;
            case 8: return KeyEvent.VK_8;
            default: return KeyEvent.VK_9;
        }
    }

    public static void main (String[] args) throws InterruptedException, AWTException {
        new ThroneResults();
    }

}
