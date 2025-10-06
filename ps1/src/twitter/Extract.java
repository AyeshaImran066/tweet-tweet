package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
            throw new IllegalArgumentException("Tweets list cannot be empty");
        }

        Instant start = tweets.get(0).getTimestamp();
        Instant end = start;

        for (Tweet tweet : tweets) {
            Instant t = tweet.getTimestamp();
            if (t.isBefore(start)) {
                start = t;
            }
            if (t.isAfter(end)) {
                end = t;
            }
        }

        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentioned = new HashSet<>();

        // Regex ensures:
        // 1. "@" must be at start of string or preceded by non-username character
        // 2. Username consists of letters, digits, underscore
        // 3. Ends at a boundary (not followed by username char)
        Pattern pattern = Pattern.compile("(?<=^|[^A-Za-z0-9_])@([A-Za-z0-9_]+)(?=$|[^A-Za-z0-9_])");

        for (Tweet tweet : tweets) {
            Matcher matcher = pattern.matcher(tweet.getText());
            while (matcher.find()) {
                mentioned.add(matcher.group(1).toLowerCase()); // normalize to lowercase
            }
        }

        return mentioned;
    }

}
