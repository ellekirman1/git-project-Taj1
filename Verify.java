import java.nio.file.Files;
import java.nio.file.Path;

//elle made tester

public class Verify {
    public static void main(String[] args) throws Exception {
        gitRepository git = new gitRepository();

        git.initializeGit();
        git.createBlob("JavaFileSystem/log.txt");

        git.writeInIndex("JavaFileSystem/log.txt");
        git.writeInIndex("JavaFileSystem/notes.txt");





    }
}