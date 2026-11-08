package filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TreeIntegrityTest {

    @Test
    void negativeFileSizeIsRejectedAndZeroIsAllowed() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -1));
        assertEquals(0, new FileNode("empty.txt", 0).sizeInBytes());
    }

    @Test
    void blankNamesAndNamesWithSlashAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(" "));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("a/b", 1));
    }

    @Test
    void duplicateSiblingNameIsRejectedAndTreeStaysUnchanged() {
        DirectoryNode dir = new DirectoryNode("dir").add(new FileNode("a.txt", 1));

        assertThrows(IllegalArgumentException.class, () -> dir.add(new FileNode("a.txt", 2)));

        assertEquals(1, dir.children().size());
    }

    @Test
    void nodeCannotHaveTwoParents() {
        FileNode file = new FileNode("shared.txt", 5);
        DirectoryNode first = new DirectoryNode("first").add(file);
        DirectoryNode second = new DirectoryNode("second");

        assertThrows(IllegalStateException.class, () -> second.add(file));

        assertTrue(second.isEmpty());
        assertSame(first, file.parent().orElseThrow());
    }

    @Test
    void directoryCannotContainItself() {
        DirectoryNode dir = new DirectoryNode("dir");

        assertThrows(IllegalArgumentException.class, () -> dir.add(dir));
    }

    @Test
    void cyclesThroughDescendantsAreRejected() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        DirectoryNode c = new DirectoryNode("c");
        a.add(b);
        b.add(c);

        assertThrows(IllegalArgumentException.class, () -> c.add(a));
        assertThrows(IllegalArgumentException.class, () -> b.add(a));
    }

    @Test
    void deepNestingAndMovingBetweenDirectoriesAreSupported() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode left = new DirectoryNode("left");
        DirectoryNode right = new DirectoryNode("right");
        root.add(left).add(right);
        left.add(new DirectoryNode("deep").add(new FileNode("f.txt", 3)));

        Node moved = left.remove("deep");
        right.add(moved);

        assertTrue(left.isEmpty());
        assertSame(right, moved.parent().orElseThrow());
    }

    @Test
    void childrenViewCannotBeModifiedFromOutside() {
        DirectoryNode dir = new DirectoryNode("dir").add(new FileNode("a.txt", 1));

        assertThrows(UnsupportedOperationException.class, () -> dir.children().clear());
    }
}