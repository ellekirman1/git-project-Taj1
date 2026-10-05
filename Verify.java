import java.nio.file.Files;
import java.nio.file.Path;

public class Verify {
    public static void main(String[] args) throws Exception {
        gitRepository git = new gitRepository();

        git.initializeGit();

        git.createBlob("JavaFileSystem/notes.txt");

        // 5. Duplicate content
        git.createBlob("JavaFileSystem/data.txt");

        // 6. Compression
        // Not implemented
    }
}