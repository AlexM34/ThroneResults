import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

        penalties.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(ThroneResults::printPlayerPenalty);
    }

    private static void printPlayerPenalty(final Map.Entry<String, Integer> entry) {
        final String player = entry.getKey();
        final Integer penalty = entry.getValue();
        System.out.println(player + " is penalised with " + penalty + " points");
    }

}
