import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class ThroneResults {
    private Map<String, Integer> POINTS = new LinkedHashMap<>();
    private Map<String, Double> MINUTES = new LinkedHashMap<>();
    private Map<String, Integer> MOVES = new LinkedHashMap<>();
    private Map<String, Double> SPEED = new LinkedHashMap<>();
    private int[] ROUNDS = new int[11];

    private ThroneResults() throws AWTException, InterruptedException, IOException, ParseException {
        stallings();
        /*getFiles();

        for (int game = 1; game < 13; game++) {
            process(game);
//            getPoints("https://www.game.thronemaster.net/?game=198753");
//            getPoints("https://www.game.thronemaster.net/?game=198768");
//            getPoints("https://www.game.thronemaster.net/?game=198755");
//            getPoints("https://www.game.thronemaster.net/?game=198756");
//            getPoints("https://www.game.thronemaster.net/?game=198757");
//            getPoints("https://www.game.thronemaster.net/?game=198762");
//            getPoints("https://www.game.thronemaster.net/?game=198763");
//            getPoints("https://www.game.thronemaster.net/?game=198759");
//            getPoints("https://www.game.thronemaster.net/?game=198782");
//            getPoints("https://www.game.thronemaster.net/?game=198764");
//            getPoints("https://www.game.thronemaster.net/?game=198760");
//            getPoints("https://www.game.thronemaster.net/?game=198816");
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
        System.out.println(String.format("Finished - %-2s", ROUNDS[10]));*/

    }

    private void stallings() throws IOException, ParseException {
        String link = "https://www.game.thronemaster.net/?game=202283&show=log";
        URL log = new URL(link);
        URLConnection yc = log.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        long DAY = TimeUnit.DAYS.toMillis(1);
        long last = Instant.now().toEpochMilli();
        Map<String, Integer> penalties = new HashMap<>();
        while ((inputLine = in.readLine()) != null) {
            Pattern pattern = Pattern.compile("<nobr>(.*?)</nobr>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(inputLine);
//            String myDate = "2014/10/29 18:10";
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//            Date date = sdf.parse(myDate);
//            System.out.println(date.getTime());
            if (matcher.find() && !matcher.group(1).equals("")) {
                String player = matcher.group(1);
                System.out.print(player + " ");
                while (!(inputLine = in.readLine()).contains("<td valign=")) {}
                inputLine = inputLine.substring(inputLine.indexOf(">") + 1);
                String time = inputLine.substring(0, inputLine.lastIndexOf("<")).replace(",", "");
                long ms = transform(time);
                System.out.println(time);
                int delay = (int) ((ms - last) / DAY);
                if (delay >= 3 ) {
                    penalties.merge(player, (delay - 2) * 3, Integer::sum);
                    System.out.println("PENALTY: " + player + " stalls for " + delay + " days until " + time);
                }

                last = ms;
            }
        }

        System.out.println();
        System.out.println("PENALTIES");
        penalties.forEach((player, penalty) -> System.out.println(player + " is penalised with " + penalty + " points"));
        in.close();
    }

    private void getPoints(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .timeout(600000)
                    .get();

        } catch (IOException ioe) {
            System.out.println("Exception!");
            ioe.printStackTrace();
        }

//        Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
//        for (Element image : images) {
//
//            System.out.println("\nsrc : " + image.attr("src"));
//            System.out.println("height : " + image.attr("height"));
//            System.out.println("width : " + image.attr("width"));
//            System.out.println("alt : " + image.attr("alt"));
//
//        }

//        System.out.println(doc.toString());
//        Elements img = document.select("#curIcon img[src]");

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

        saveAs(opt, 1, "https://www.game.thronemaster.net/?game=198753");
//        saveAs(opt, 2, "https://www.game.thronemaster.net/?game=198768");
        saveAs(opt, 3, "https://www.game.thronemaster.net/?game=198755");
        saveAs(opt, 4, "https://www.game.thronemaster.net/?game=198756");
        saveAs(opt, 5, "https://www.game.thronemaster.net/?game=198757");
        saveAs(opt, 6, "https://www.game.thronemaster.net/?game=198762");
        saveAs(opt, 7, "https://www.game.thronemaster.net/?game=198763");
        saveAs(opt, 8, "https://www.game.thronemaster.net/?game=198759");
//        saveAs(opt, 9, "https://www.game.thronemaster.net/?game=198782");
//        saveAs(opt, 10, "https://www.game.thronemaster.net/?game=198764");
        saveAs(opt, 11, "https://www.game.thronemaster.net/?game=198760");
//        saveAs(opt, 12, "https://www.game.thronemaster.net/?game=198816");
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

//        driver.close();
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

    private long transform(final String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse(transformMonth(time));
        long millis = date.getTime();
//        System.out.println(millis);
        return millis;
    }

    private String transformMonth(final String time) {
//        System.out.println(time);
        Pattern pattern = Pattern.compile("-(.*?)-", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(time);
        String month = matcher.find() ? matcher.group(1) : null;
        String swap = null;
        switch (month) {
            case "Jan": swap = "01"; break;
            case "Feb": swap = "02"; break;
            case "Mar": swap = "03"; break;
            case "Apr": swap = "04"; break;
            case "May": swap = "05"; break;
            case "Jun": swap = "06"; break;
            case "Jul": swap = "07"; break;
            case "Aug": swap = "08"; break;
            case "Sep": swap = "09"; break;
            case "Oct": swap = "10"; break;
            case "Nov": swap = "11"; break;
            case "Dec": swap = "12"; break;
        }
//        System.out.println(month);
//        System.out.println(swap);

        return time.replace(month, swap);
    }

    public static void main (String[] args) throws InterruptedException, AWTException, IOException, ParseException {
        new ThroneResults();
    }
}
