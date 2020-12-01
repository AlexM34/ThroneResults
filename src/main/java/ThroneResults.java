import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThroneResults {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "<title>AGoT Game Log - (.*?)</title>", Pattern.DOTALL);
    private static final Pattern PLAYER_PATTERN = Pattern.compile("<nobr>(.*?)</nobr>", Pattern.DOTALL);
    private static final long DAY_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final int DAYS_ALLOWED = 2;
    private static final int PENALTY_PER_DAY = 2;

    private final Map<String, Integer> POINTS = new LinkedHashMap<>();
    private final Map<String, Double> MINUTES = new LinkedHashMap<>();
    private final Map<String, Integer> MOVES = new LinkedHashMap<>();
    private final Map<String, Double> SPEED = new LinkedHashMap<>();
    private final int[] ROUNDS = new int[11];

    public static void main (final String[] args) throws IOException, ParseException {
        stallings();
    }

    private static void stallings() throws IOException, ParseException {

        final int[] ids = {245387,245460,245377,245518,245487,245643,245470,245457,245478};
        final Map<String, Integer> penalties = new HashMap<>();

        for (final int id : ids) processGame(id, penalties);

        printPenalties(penalties);
    }

    private static void processGame(final int id, final Map<String, Integer> penalties)
            throws IOException, ParseException {

        final BufferedReader reader = getReader(id);
        printGameTitle(reader);

        long lastMoveMs = Instant.now().toEpochMilli();
        String inputLine;

        while ((inputLine = reader.readLine()) != null) {
            final Matcher matcher = PLAYER_PATTERN.matcher(inputLine);
            if (matcher.find() && !matcher.group(1).equals("")) {
                final String player = matcher.group(1);
                do {
                    inputLine = reader.readLine();
                } while (!inputLine.contains("<td valign="));

                final String time = inputLine.substring(inputLine.indexOf(">") + 1,
                        inputLine.lastIndexOf("<")).replace(",", "");

                final long currentMoveMs = getMillis(time);
                final int delay = (int) ((currentMoveMs - lastMoveMs) / DAY_MILLIS);

                if (delay >= DAYS_ALLOWED) {
                    penalties.merge(player, (delay - 1) * PENALTY_PER_DAY, Integer::sum);
                    System.out.println(player + " stalls for " + delay + " days until " + time);
                }

                lastMoveMs = currentMoveMs;
            }
        }

        reader.close();
    }

    private static BufferedReader getReader(final int id) throws IOException {
        final String link = String.format("https://www.game.thronemaster.net/?game=%d&show=log", id);
        final URL log = new URL(link);
        final URLConnection connection = log.openConnection();
        final Reader inputReader = new InputStreamReader(connection.getInputStream());
        return new BufferedReader(inputReader);
    }

    private static void printGameTitle(final BufferedReader reader) throws IOException {
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            if (inputLine.contains("<title>")) {
                final Matcher matcher = TITLE_PATTERN.matcher(inputLine);

                if (matcher.find()) {
                    System.out.println();
                    System.out.println(matcher.group(1));
                    return;
                }
            }
        }
    }

    private static long getMillis(final String time) throws ParseException {
        Date date = DATE_FORMAT.parse(transformMonth(time));
        return date.getTime();
    }

    private static String transformMonth(final String time) {
        final Pattern pattern = Pattern.compile("-(.*?)-", Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(time);
        final String month = matcher.find() ? matcher.group(1) : "";
        final String numeric;

        switch (month) {
            case "Jan": numeric = "01"; break;
            case "Feb": numeric = "02"; break;
            case "Mar": numeric = "03"; break;
            case "Apr": numeric = "04"; break;
            case "May": numeric = "05"; break;
            case "Jun": numeric = "06"; break;
            case "Jul": numeric = "07"; break;
            case "Aug": numeric = "08"; break;
            case "Sep": numeric = "09"; break;
            case "Oct": numeric = "10"; break;
            case "Nov": numeric = "11"; break;
            case "Dec": numeric = "12"; break;
            default: numeric = "";
        }

        return time.replace(month, numeric);
    }

    private static void printPenalties(final Map<String, Integer> penalties) {
        System.out.println();
        System.out.println("PENALTIES");

        penalties.entrySet().stream().sorted(comparingByValue(Comparator.reverseOrder()))
                .forEach(ThroneResults::printPlayerPenalty);
    }

    private static void printPlayerPenalty(final Map.Entry<String, Integer> entry) {
        final String player = entry.getKey();
        final Integer penalty = entry.getValue();
        System.out.println(player + " is penalised with " + penalty + " points");
    }

    private ThroneResults() throws IOException, ParseException {
//        getFiles();
        stallings();

        for (int game = 1; game < 7; game++) {
            process(game);
//            getPoints("https://www.game.thronemaster.net/?game=205948");
//            getPoints("https://www.game.thronemaster.net/?game=205949");
//            getPoints("https://www.game.thronemaster.net/?game=205950");
//            getPoints("https://www.game.thronemaster.net/?game=205951");
//            getPoints("https://www.game.thronemaster.net/?game=205952");
//            getPoints("https://www.game.thronemaster.net/?game=205953");
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
        System.out.println(String.format("Finished - %-2s", ROUNDS[10]));
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

        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperapps.neverAsk.saveToDisk","jpeg");
        profile.setPreference("browser.download.dir", "C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\games");
        FirefoxOptions opt = new FirefoxOptions();
        opt.setProfile(profile);

        saveAs(opt, 1, "https://www.game.thronemaster.net/?game=205948");
        saveAs(opt, 2, "https://www.game.thronemaster.net/?game=205949");
        saveAs(opt, 3, "https://www.game.thronemaster.net/?game=205950");
        saveAs(opt, 4, "https://www.game.thronemaster.net/?game=205951");
        saveAs(opt, 5, "https://www.game.thronemaster.net/?game=205952");
        saveAs(opt, 6, "https://www.game.thronemaster.net/?game=205953");
    }

    private void saveAs(final FirefoxOptions opt, final int number, final String link)
            throws AWTException, InterruptedException {

        final WebDriver driver = new FirefoxDriver(opt);
        driver.get(link);

        final Robot robot = new Robot();
        pressEnter(robot);
        openSaveWindow(robot);
        enterName(robot, number);
        pressEnter(robot);
        pressLeft(robot);
        pressEnter(robot);

        Thread.sleep(5000L);
    }

    private void pressEnter(final Robot robot) throws InterruptedException {
        Thread.sleep(3000L);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private void openSaveWindow(final Robot robot) throws InterruptedException {
        Thread.sleep(3000L);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private void enterName(final Robot robot, final int number) throws InterruptedException {
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
    }

    private void pressLeft(final Robot robot) throws InterruptedException {
        Thread.sleep(2000L);

        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
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

}
