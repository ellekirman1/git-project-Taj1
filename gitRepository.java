import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class gitRepository {

    public static void main(String[] args) {

        gitRepository git = new gitRepository();

        try {
            // Test 1: Initialize repository
            git.initializeGit();

            // Test 2: Verify required files/directories exist
            Path repository = Path.of("git");
            Path objects = repository.resolve("objects");
            Path index = repository.resolve("index");
            Path head = repository.resolve("HEAD");

            if (Files.isDirectory(repository)
                    && Files.isDirectory(objects)
                    && Files.isRegularFile(index)
                    && Files.isRegularFile(head)) {

                System.out.println("Test Passed: Git repository initialized");

            } else {
                System.out.println("Test failed: repository is missing files/directories");
            }

        } catch (IOException e) {
            System.out.println("Test Failed: " + e.getMessage());
        }

        try {
            String hash = FileHasher.hashFile("JavaFileSystem/backup/backup.txt");
            System.out.println("SHA-1: " + hash);
        } catch (IOException e) {
            System.out.println("Hash error: " + e.getMessage());
        }
    }

    public void initializeGit() throws IOException { // this is the "init" function
        Path git = Path.of("git");
        Path objects = git.resolve("objects");
        Path index = git.resolve("index");
        Path head = git.resolve("HEAD");

        if (!Files.exists(git)) {
            Files.createDirectory(git);
        }

        if (!Files.exists(objects)) {
            Files.createDirectory(objects);
        }

        if (!Files.exists(index)) {
            Files.createFile(index);
        }

        if (!Files.exists(head)) {
            Files.createFile(head);
        }

        if (Files.exists(git) && Files.exists(objects) && Files.exists(index) && Files.exists(head)) {
            System.out.println("Git Repository Already Exists");
        } else {
            System.out.println("Git Repository Created");
        }

    }

    public String createBlob(String filePath) throws IOException {
        initializeGit(); // always have to do this

        Path source = Path.of(filePath);

        if (!Files.isRegularFile(source)) {
            throw new IOException("No such file: " + filePath);
        }
        String hash = FileHasher.hashFile(filePath);
        Path blob = Path.of("git", "objects", hash);
        byte[] content = Files.readAllBytes(source);
        Files.write(blob, content);
        return hash;
    }

}
