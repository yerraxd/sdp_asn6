package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import filesystem.visitors.TotalSizeVisitor;
import org.junit.jupiter.api.Test;

class TotalSizeVisitorTest {

    @Test
    void sumsFilesAcrossSeveralNestingLevels() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        visitor.runOn(TestTrees.sampleTree());

        assertEquals(6844, visitor.totalBytes());
    }

    @Test
    void worksOnAnySubtree() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        visitor.runOn(TestTrees.docsDirectory());

        assertEquals(2668, visitor.totalBytes());
    }

    @Test
    void emptyDirectoryHasZeroSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        visitor.runOn(new DirectoryNode("empty"));

        assertEquals(0, visitor.totalBytes());
    }

    @Test
    void singleFileAsRootCountsOnce() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        visitor.runOn(new FileNode("only.bin", 7));

        assertEquals(7, visitor.totalBytes());
    }
}