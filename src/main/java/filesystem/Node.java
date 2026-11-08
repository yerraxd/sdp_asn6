package filesystem;

import java.util.Optional;
public abstract class Node implements Element {
    private final String name;
    private DirectoryNode parent;

    protected Node(String name) {
        this.name = requireValidName(name);
    }

    private static String requireValidName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (name.contains("/")) {
            throw new IllegalArgumentException("Name must not contain '/': " + name);
        }
        return name;
    }

    @Override
    public String name() {
        return name;
    }

    public Optional<DirectoryNode> parent() {
        return Optional.ofNullable(parent);
    }

    DirectoryNode parentOrNull() {
        return parent;
    }

    void setParent(DirectoryNode parent) {
        this.parent = parent;
    }
}