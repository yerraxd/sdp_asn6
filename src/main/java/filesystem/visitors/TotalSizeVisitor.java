package filesystem.visitors;

import filesystem.FileNode;

public final class TotalSizeVisitor extends TraversingVisitor {
    private long totalBytes;

    public long totalBytes() {
        return totalBytes;
    }

    @Override
    protected void resetState() {
        totalBytes = 0;
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        totalBytes += file.sizeInBytes();
    }
}