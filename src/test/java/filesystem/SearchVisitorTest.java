package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import filesystem.visitors.SearchVisitor;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchVisitorTest {

    private static List<String> search(Node root, String extension, long minSize) {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(extension), minSize);
        visitor.runOn(root);
        return visitor.matchingPaths();
    }

    @Test
    void bothConditionsAreCombined() {
        List<String> result = search(TestTrees.sampleTree(), ".txt", 100);

        assertEquals(List.of("docs/archive/old.txt", "docs/notes.txt"), result);
    }

    @Test
    void sizeThresholdIsInclusive() {
        assertEquals(List.of("docs/archive/old.txt", "docs/notes.txt"),
                search(TestTrees.sampleTree(), ".txt", 120));
        assertEquals(List.of("docs/archive/old.txt"),
                search(TestTrees.sampleTree(), ".txt", 121));
    }

    @Test
    void sizeConditionAloneFiltersWhenNameAcceptsEverything() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 2048);

        visitor.runOn(TestTrees.sampleTree());

        assertEquals(List.of("docs/report.pdf", "media/photo.jpg"), visitor.matchingPaths());
    }

    @Test
    void pathsAreSortedLexicographicallyAsWholePaths() {
        List<String> result = search(TestTrees.sampleTree(), ".txt", 0);

        assertEquals(List.of(
                "a-b/x.txt",
                "a/x.txt",
                "docs/archive/old.txt",
                "docs/notes.txt",
                "readme.txt"), result);
    }

    @Test
    void pathsAreRelativeToTheChosenRoot() {
        List<String> result = search(TestTrees.docsDirectory(), ".txt", 0);

        assertEquals(List.of("archive/old.txt", "notes.txt"), result);
    }

    @Test
    void rootFileIsReportedByItsName() {
        List<String> result = search(new FileNode("solo.txt", 10), ".txt", 0);

        assertEquals(List.of("solo.txt"), result);
    }

    @Test
    void noMatchAndEmptyStructuresGiveEmptyResult() {
        assertTrue(search(TestTrees.sampleTree(), ".pdf", 3000).isEmpty());
        assertTrue(search(new DirectoryNode("empty"), ".txt", 0).isEmpty());
    }

    @Test
    void negativeThresholdIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
    }
}