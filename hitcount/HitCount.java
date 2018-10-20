package hitcount;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class HitCount {

    // Term definitions:
    // N: Number of rows in the input file
    // W: Number of unique websites in the file
    // D: Number of unique days in the timestamps in the file

    // Runtime Complexity: O(N + D * log(D) + D * W * log(W))

    // Explanation:

    // O(N):                We read in each line in the input file, then do some number of hash table lookups to add it
    //                      into hitCountsByDay
    // O(D * log(D)):       This is from sorting the days before we start printing things out
    // O(D * W * log(W)):   This is the cost of sorting the hit counts for each day.  It could be a bit lower, since
    //                      W is just an upper bound on the number of websites visited each day, but it depends on
    //                      how many rarely visited websites are in the file.


    // Normally I'd use guava's Preconditions version of this, but I wanted to avoid using non standard libs
    private static void checkArgument(boolean check, String message) {
        if(!check) {
            throw new IllegalArgumentException(message);
        }
    }

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    // removes millis beyond the start of the day
    private static long truncateDay(long millis) {
        long daysSinceEpoch = millis / DAY_IN_MILLIS;
        return daysSinceEpoch * DAY_IN_MILLIS;
    }

    private static void addToHitCounts(long day, String website, Map<Long, Map<String, Long>> hitCountsByDay) {
        if(!hitCountsByDay.containsKey(day)) {
            hitCountsByDay.put(day, new HashMap<>());
        }
        Map<String, Long> hitCounts = hitCountsByDay.get(day);
        if(!hitCounts.containsKey(website)) {
            hitCounts.put(website, 0L);
        }
        hitCounts.put(website, hitCounts.get(website) + 1);
    }

    private static Map<Long, Map<String, Long>> countHitsByDay(Stream<String> lines) {
        Map<Long, Map<String, Long>> hitCountsByDay = new HashMap<>();
        lines.forEach(line -> {
            String[] parts = line.split("\\|");
            checkArgument(parts.length == 2, "Each line in input file must have exactly one '|' character.  Offending line: " + line);
            long day = truncateDay(Long.parseLong(parts[0]));
            String website = parts[1];
            addToHitCounts(day, website, hitCountsByDay);
        });
        return hitCountsByDay;
    }

    private static void printDay(SimpleDateFormat dateFormat, long day) {
        String formattedDate = dateFormat.format(new Date(day));
        System.out.println(formattedDate + " GMT");
    }

    private static Iterable<Map.Entry<String, Long>> getOrderedEntrySet(Map<String, Long> hitCounts) {
        Comparator<Map.Entry<String, Long>> hitCountAscending = Comparator.comparing(Map.Entry::getValue);
        Comparator<Map.Entry<String, Long>> hitCountDescending = hitCountAscending.reversed();
        List<Map.Entry<String, Long>> entries = new ArrayList<>(hitCounts.entrySet());
        entries.sort(hitCountDescending);
        return entries;
    }

    private static void printHitCounts(Map<String, Long> hitCounts) {
        Iterable<Map.Entry<String, Long>> sortedEntries = getOrderedEntrySet(hitCounts);
        sortedEntries.forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));
    }

    private static void printHitCountsByDay(Map<Long, Map<String, Long>> hitCountsByDay) {
        SortedSet<Long> sortedDays = new TreeSet<>(hitCountsByDay.keySet());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        for(long day : sortedDays) {
            printDay(dateFormat, day);
            printHitCounts(hitCountsByDay.get(day));
        }
    }

    public static void main(String[] args) throws IOException {
        checkArgument(args.length == 1, "Input should be filename");
        String filename = args[0];
        Stream<String> lines = Files.lines(Paths.get(filename));
        Map<Long, Map<String, Long>> hitCountsByDay = countHitsByDay(lines);
        printHitCountsByDay(hitCountsByDay);
    }
}
