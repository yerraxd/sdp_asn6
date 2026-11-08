package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DoubleDispatchTest {
    private static final class RecordingVisitor implements Visitor {
        private final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.name());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("directory:" + directory.name());
        }
    }

    @Test
    void fileReachesFileOverload() {
        RecordingVisitor visitor = new RecordingVisitor();

        new FileNode("a.txt", 1).accept(visitor);

        assertEquals(List.of("file:a.txt"), visitor.calls);
    }

    @Test
    void directoryReachesDirectoryOverloadAndAcceptDoesNotTraverse() {
        RecordingVisitor visitor = new RecordingVisitor();
        DirectoryNode dir = new DirectoryNode("d").add(new FileNode("inner.txt", 1));

        dir.accept(visitor);

        assertEquals(List.of("directory:d"), visitor.calls);
    }

    @Test
    void dispatchDependsOnRuntimeTypeNotOnStaticType() {
        RecordingVisitor visitor = new RecordingVisitor();
        Element asFile = new FileNode("f", 1);
        Element asDirectory = new DirectoryNode("d");

        asFile.accept(visitor);
        asDirectory.accept(visitor);

        assertEquals(List.of("file:f", "directory:d"), visitor.calls);
    }
}