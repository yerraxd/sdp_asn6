package filesystem;

final class TestTrees {
    private TestTrees() {
    }

    static DirectoryNode docsDirectory() {
        return new DirectoryNode("docs")
                .add(new FileNode("notes.txt", 120))
                .add(new FileNode("report.pdf", 2048))
                .add(new DirectoryNode("archive").add(new FileNode("old.txt", 500)));
    }

    static DirectoryNode sampleTree() {
        return new DirectoryNode("root")
                .add(docsDirectory())
                .add(new DirectoryNode("media").add(new FileNode("photo.jpg", 4096)))
                .add(new DirectoryNode("empty"))
                .add(new DirectoryNode("a-b").add(new FileNode("x.txt", 10)))
                .add(new DirectoryNode("a").add(new FileNode("x.txt", 20)))
                .add(new FileNode("readme.txt", 50));
    }
}