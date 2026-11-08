package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import filesystem.visitors.StatisticsVisitor;
import org.junit.jupiter.api.Test;

class StatisticsVisitorTest {

    @Test
    void countsAndDepthForNestedTree() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        visitor.runOn(TestTrees.sampleTree());

        assertEquals(7, visitor.fileCount());
        assertEquals(7, visitor.directoryCount());
        assertEquals(3, visitor.maxDepth());
    }

    @Test
    void emptyRootDirectoryCountsItselfAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        visitor.runOn(new DirectoryNode("empty"));

        assertEquals(0, visitor.fileCount());
        assertEquals(1, visitor.directoryCount());
        assertEquals(0, visitor.maxDepth());
    }

    @Test
    void singleFileAsRootHasDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        visitor.runOn(new FileNode("solo", 1));

        assertEquals(1, visitor.fileCount());
        assertEquals(0, visitor.directoryCount());
        assertEquals(0, visitor.maxDepth());
    }

    @Test
    void chainOfEmptyDirectoriesDeterminesDepth() {
        DirectoryNode root = new DirectoryNode("a")
                .add(new DirectoryNode("b")
                        .add(new DirectoryNode("c")
                                .add(new DirectoryNode("d"))));
        StatisticsVisitor visitor = new StatisticsVisitor();

        visitor.runOn(root);

        assertEquals(4, visitor.directoryCount());
        assertEquals(3, visitor.maxDepth());
    }
}