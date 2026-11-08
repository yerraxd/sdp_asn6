package filesystem;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode add(Node child) {
        Objects.requireNonNull(child, "child");
        if (children.containsKey(child.name())) {
            throw new IllegalArgumentException(
                    "'" + name() + "' already contains a child named '" + child.name() + "'");
        }
        children.put(child.name(), child);
        return this;
    }

    public Collection<Node> children() {
        return Collections.unmodifiableCollection(children.values());
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}