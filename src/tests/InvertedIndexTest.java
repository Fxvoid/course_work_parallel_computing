package tests;

import logic.InvertedIndexManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvertedIndexTest {
    InvertedIndexManager invertedIndexManager;

    @BeforeAll
    public void setUp() {
        invertedIndexManager = new InvertedIndexManager();
    }

    @Test
    void testIndexCreationSingleThread() throws IOException {
        invertedIndexManager.createInvertedIndex(1);
    }

    @Test
    void testIndexCreationTwoThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(2);
    }

    @Test
    void testIndexCreationFourThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(4);
    }

    @Test
    void testIndexCreationEightThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(8);
    }

    @Test
    void testIndexCreationTenThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(10);
    }

    @Test
    void testIndexCreationTwentyThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(20);
    }

    @Test
    void testIndexCreationFiftyThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(50);
    }

    @Test
    void testIndexCreationHundredThreads() throws IOException {
        invertedIndexManager.createInvertedIndex(100);
    }

    @Test
    void testSearch() throws RemoteException {
        String result = invertedIndexManager.search("fsfasrews");
        assertEquals("No data matching your request found :(", result);
        result = invertedIndexManager.search("ths reqst shud make abslutely no sense");
        assertEquals("No data matching your request found :(", result);
        result = invertedIndexManager.search("...(sdadasdd)|{LDS plshelp$$$");
        assertEquals("No data matching your request found :(", result);
        result = invertedIndexManager.search("It's truly headache-inducing.<br /><br />");
        assertNotEquals("No data matching your request found :(", result);
        result = invertedIndexManager.search("Get it? Get it yet? Get it now?");
        assertNotEquals("No data matching your request found :(", result);
        result = invertedIndexManager.search("Some college kid reporters go off in search of a hidden ghost town called Acheron." +
                                                            " Naturally they find a bit more than they bargained for. After a good start the movie gets pretty bogged down." +
                                                            " During the first half it's kind of hard to figure out much about what's really going on, it just plain jumps around too much." +
                                                            " There are flashbacks that don't really help since they just sort of pop up out of nowhere." +
                                                            " The last half hour or so picks up nicely as people are getting knocked off left and right in very violent fashion." +
                                                            " You're gonna have to watch this sucker twice to actually enjoy this flick." +
                                                            " That way you'll have some idea where the plot is going." +
                                                            " Watchable if you like blood and guts." +
                                                            " I give it a four out of ten for gore.");
        assertNotEquals("No data matching your request found :(", result);
    }

}