package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import filesystem.visitors.SearchVisitor;
import filesystem.visitors.StatisticsVisitor;
import filesystem.visitors.TotalSizeVisitor;
import java.util.List;
import org.junit.jupiter.api.Test;

class VisitorStateTest {

    @Test
    void runningTotalSizeTwiceDoesNotDoubleTheResult() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        DirectoryNode tree = TestTrees.sampleTree();

        visitor.runOn(tree);
        visitor.runOn(tree);

        assertEquals(6844, visitor.totalBytes());
    }

    @Test
    void reusingVisitorOnAnotherTreeDoesNotLeakOldResults() {
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 0);
        StatisticsVisitor stats = new StatisticsVisitor();

        size.runOn(TestTrees.sampleTree());
        search.runOn(TestTrees.sampleTree());
        stats.runOn(TestTrees.sampleTree());
        size.runOn(TestTrees.docsDirectory());
        search.runOn(TestTrees.docsDirectory());
        stats.runOn(TestTrees.docsDirectory());

        assertEquals(2668, size.totalBytes());
        assertEquals(List.of("archive/old.txt", "notes.txt"), search.matchingPaths());
        assertEquals(3, stats.fileCount());
        assertEquals(2, stats.directoryCount());
        assertEquals(2, stats.maxDepth());
    }

    @Test
    void resultDoesNotDependOnRunOrder() {
        StatisticsVisitor reused = new StatisticsVisitor();
        reused.runOn(TestTrees.docsDirectory());
        reused.runOn(TestTrees.sampleTree());
        StatisticsVisitor fresh = new StatisticsVisitor();
        fresh.runOn(TestTrees.sampleTree());

        assertEquals(fresh.fileCount(), reused.fileCount());
        assertEquals(fresh.directoryCount(), reused.directoryCount());
        assertEquals(fresh.maxDepth(), reused.maxDepth());
    }

    @Test
    void searchResultIsASnapshotThatCannotBeModified() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        visitor.runOn(TestTrees.docsDirectory());

        List<String> result = visitor.matchingPaths();

        assertThrows(UnsupportedOperationException.class, () -> result.add("hacked"));
        assertEquals(3, visitor.matchingPaths().size());
    }
}