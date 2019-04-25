import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ThroneResults {

    private ThroneResults() {

        Map<String, Integer> points = new HashMap<>();
        File input = new File("C:\\Users\\A.Monev\\IdeaProjects\\ThroneResults\\20.html");
        Document doc = null;

        try {
            doc = Jsoup.parse(input, "UTF-8", "");
            //doc = Jsoup.connect("https://www.game.thronemaster.net/?game=191508.html").get();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Elements elements = doc.select("div#stats_ladder");
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

        points.forEach((player, score) ->
                System.out.println(player + " " + score));

    }

    public static void main (String args[]) {
        new ThroneResults();
    }

}
