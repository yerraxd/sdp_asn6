package filesystem.visitors;

import filesystem.FileNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor extends TraversingVisitor {
    private final Predicate<String> nameCriterion;
    private final long minimumSizeInBytes;
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(Predicate<String> nameCriterion, long minimumSizeInBytes) {
        this.nameCriterion = Objects.requireNonNull(nameCriterion, "nameCriterion");
        if (minimumSizeInBytes < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative: " + minimumSizeInBytes);
        }
        this.minimumSizeInBytes = minimumSizeInBytes;
    }

    public List<String> matchingPaths() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    @Override
    protected void resetState() {
        matches.clear();
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        boolean bigEnough = file.sizeInBytes() >= minimumSizeInBytes;
        if (bigEnough && nameCriterion.test(file.name())) {
            matches.add(relativePath);
        }
    }
}