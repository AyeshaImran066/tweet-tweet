/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */


package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    /*
     * Testing strategy:
     *
     * getTimespan():
     *  - one tweet
     *  - multiple tweets at different times
     *  - multiple tweets all at the same time
     *
     * getMentionedUsers():
     *  - no mentions
     *  - one mention
     *  - multiple mentions in one tweet
     *  - mentions across multiple tweets
     *  - repeated mentions of same user (should only appear once in set)
     *  - case differences in mentions (@Alice vs @alice, should normalize)
     *  - text with '@' but not a valid mention (like email addresses)
     */

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    @Test
    public void testGetTimespanSingleTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals("expected start == end", d1, timespan.getStart());
        assertEquals("expected start == end", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanSameTimeTweets() {
        Tweet tweet3 = new Tweet(3, "charlie", "another tweet at same time", d1);
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hello @bob", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue(mentionedUsers.contains("bob"));
        assertEquals(1, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsSameTweet() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hi @bob and @charlie", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue(mentionedUsers.contains("bob"));
        assertTrue(mentionedUsers.contains("charlie"));
        assertEquals(2, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersAcrossTweets() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hi @bob", d1);
        Tweet tweet4 = new Tweet(4, "bbitdiddle", "hi @charlie", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet4));
        assertTrue(mentionedUsers.contains("bob"));
        assertTrue(mentionedUsers.contains("charlie"));
        assertEquals(2, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersCaseInsensitiveAndDeduped() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hi @Alice and @ALICE", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue(mentionedUsers.contains("alice"));  // should be lowercase
        assertEquals(1, mentionedUsers.size());        // deduplicated
    }

    @Test
    public void testGetMentionedUsersIgnoresEmail() {
        Tweet tweet3 = new Tweet(3, "alyssa", "email me at bob@example.com", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue(mentionedUsers.isEmpty());
    }


    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
