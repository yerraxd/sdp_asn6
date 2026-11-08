package filesystem;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;

public final class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }
    public DirectoryNode add(Node child) {
        Objects.requireNonNull(child, "child");
        rejectCycle(child);
        rejectSecondParent(child);
        rejectDuplicateName(child);

        children.put(child.name(), child);
        child.setParent(this);
        return this;
    }

    public Node remove(String childName) {
        Node removed = children.remove(childName);
        if (removed == null) {
            throw new NoSuchElementException("No child named '" + childName + "' in '" + name() + "'");
        }
        removed.setParent(null);
        return removed;
    }

    public Collection<Node> children() {
        return Collections.unmodifiableCollection(children.values());
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    private void rejectCycle(Node child) {
        for (DirectoryNode current = this; current != null; current = current.parentOrNull()) {
            if (current == child) {
                throw new IllegalArgumentException(
                        "Adding '" + child.name() + "' to '" + name() + "' would create a cycle");
            }
        }
    }

    private void rejectSecondParent(Node child) {
        if (child.parentOrNull() != null) {
            throw new IllegalStateException(
                    "'" + child.name() + "' already belongs to '" + child.parentOrNull().name() + "'");
        }
    }

    private void rejectDuplicateName(Node child) {
        if (children.containsKey(child.name())) {
            throw new IllegalArgumentException(
                    "'" + name() + "' already contains a child named '" + child.name() + "'");
        }
    }
}