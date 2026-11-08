package filesystem.visitors;

import filesystem.DirectoryNode;
import filesystem.Element;
import filesystem.FileNode;
import filesystem.Node;
import filesystem.Visitor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public abstract class TraversingVisitor implements Visitor {
    private final Deque<String> directoryNames = new ArrayDeque<>();
    private int currentDepth = -1;

    public final void runOn(Element root) {
        Objects.requireNonNull(root, "root");
        currentDepth = -1;
        directoryNames.clear();
        resetState();
        root.accept(this);
    }

    @Override
    public final void visit(FileNode file) {
        onFile(file, relativePathOf(file), currentDepth + 1);
    }

    @Override
    public final void visit(DirectoryNode directory) {
        currentDepth++;
        boolean isRoot = currentDepth == 0;
        if (!isRoot) {
            directoryNames.addLast(directory.name());
        }

        onDirectory(directory, currentDepth);
        for (Node child : directory.children()) {
            child.accept(this);
        }

        if (!isRoot) {
            directoryNames.removeLast();
        }
        currentDepth--;
    }

    protected void resetState() {
    }

    protected void onFile(FileNode file, String relativePath, int depth) {
    }

    protected void onDirectory(DirectoryNode directory, int depth) {
    }

    private String relativePathOf(FileNode file) {
        if (directoryNames.isEmpty()) {
            return file.name();
        }
        return String.join("/", directoryNames) + "/" + file.name();
    }
}