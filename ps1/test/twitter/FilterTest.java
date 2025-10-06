package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy:
     *
     * writtenBy():
     *   - username exists / does not exist
     *   - username case differs
     *   - empty tweet list
     *   - multiple tweets by same author
     *
     * inTimespan():
     *   - tweet before, within, after timespan
     *   - timespan includes exact start/end timestamps
     *   - empty tweet list
     *
     * containing():
     *   - tweet contains word / does not contain word
     *   - case-insensitive
     *   - punctuation next to word
     *   - multiple search words
     *   - empty tweet list
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "ALYSSA", "Java is fun! #programming", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // --- writtenBy tests ---
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        assertEquals(2, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
        assertTrue(writtenBy.contains(tweet3));
    }

    @Test
    public void testWrittenByNoMatches() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "charlie");
        assertTrue(writtenBy.isEmpty());
    }

    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(Collections.emptyList(), "alyssa");
        assertTrue(writtenBy.isEmpty());
    }

    // --- inTimespan tests ---
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant start = Instant.parse("2016-02-17T09:00:00Z");
        Instant end = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(start, end));
        assertFalse(inTimespan.isEmpty());
        assertTrue(inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals(0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testInTimespanExactBoundaries() {
        Timespan ts = new Timespan(d1, d2);
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), ts);
        assertEquals(2, inTimespan.size());
        assertTrue(inTimespan.contains(tweet1));
        assertTrue(inTimespan.contains(tweet2));
        assertFalse(inTimespan.contains(tweet3));
    }

    @Test
    public void testInTimespanNoMatches() {
        Timespan ts = new Timespan(Instant.parse("2016-02-17T00:00:00Z"), Instant.parse("2016-02-17T09:00:00Z"));
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), ts);
        assertTrue(inTimespan.isEmpty());
    }

    @Test
    public void testInTimespanEmptyList() {
        Timespan ts = new Timespan(d1, d2);
        List<Tweet> inTimespan = Filter.inTimespan(Collections.emptyList(), ts);
        assertTrue(inTimespan.isEmpty());
    }

    // --- containing tests ---
    @Test
    public void testContainingSingleWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertEquals(2, containing.size());
        assertTrue(containing.contains(tweet1));
        assertTrue(containing.contains(tweet2));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("java"));
        assertEquals(1, containing.size());
        assertTrue(containing.contains(tweet3));
    }

    @Test
    public void testContainingWithPunctuation() {
        Tweet tweetPunct = new Tweet(4, "bob", "I love Java!", d1);
        List<Tweet> containing = Filter.containing(Arrays.asList(tweetPunct), Arrays.asList("java"));
        assertEquals(1, containing.size());
        assertTrue(containing.contains(tweetPunct));
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk", "java"));
        assertEquals(3, containing.size());
        assertTrue(containing.contains(tweet1));
        assertTrue(containing.contains(tweet2));
        assertTrue(containing.contains(tweet3));
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("python"));
        assertTrue(containing.isEmpty());
    }

    @Test
    public void testContainingEmptyList() {
        List<Tweet> containing = Filter.containing(Collections.emptyList(), Arrays.asList("talk"));
        assertTrue(containing.isEmpty());
    }
}
