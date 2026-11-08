package filesystem;

import filesystem.visitors.SearchVisitor;
import filesystem.visitors.StatisticsVisitor;
import filesystem.visitors.TotalSizeVisitor;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        DirectoryNode project = buildProjectTree();

        TotalSizeVisitor size = new TotalSizeVisitor();
        size.runOn(project);
        System.out.println("Total size: " + size.totalBytes() + " bytes");

        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".java"), 1000);
        search.runOn(project);
        System.out.println("Java files >= 1000 bytes:");
        search.matchingPaths().forEach(path -> System.out.println("  " + path));

        StatisticsVisitor stats = new StatisticsVisitor();
        stats.runOn(project);
        System.out.println("Files: " + stats.fileCount()
                + ", directories: " + stats.directoryCount()
                + ", max depth: " + stats.maxDepth());
    }

    private static DirectoryNode buildProjectTree() {
        return new DirectoryNode("project")
                .add(new FileNode("README.md", 1200))
                .add(new FileNode("pom.xml", 2100))
                .add(new DirectoryNode("src")
                        .add(new DirectoryNode("main")
                                .add(new FileNode("App.java", 3400))
                                .add(new FileNode("Util.java", 900)))
                        .add(new DirectoryNode("test")
                                .add(new FileNode("AppTest.java", 1800))))
                .add(new DirectoryNode("docs").add(new FileNode("design.md", 4500)))
                .add(new DirectoryNode("target"));
    }
}