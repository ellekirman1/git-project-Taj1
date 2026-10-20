import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class gitRepository {

    public static void main(String[] args) {

        gitRepository git = new gitRepository();

        try {
            // Test 1: Initialize repository
            git.initializeGit();

            // Test 2: Verify required files/directories exist
            Path directory = Path.of("git");
            Path objects = directory.resolve("objects");
            Path index = directory.resolve("index");
            Path head = directory.resolve("HEAD");

            if (Files.isDirectory(directory)
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



        //test tree:
        try {
    List<String> workingList = new ArrayList<>();

    workingList.add("blob abc123 myProgram/docs/Hello.txt");
    workingList.add("blob def456 myProgram/docs/World.txt");
    workingList.add("blob ghi789 myProgram/README.md");

    String hash1 = git.createTree(workingList, "myProgram/docs");
    String hash2 = git.createTree(workingList, "myProgram/docs");

    System.out.println("Tree exists: " +
        Files.exists(Paths.get("git/objects/" + hash1)));

    System.out.println("Correct contents: " +
        Files.readString(Paths.get("git/objects/" + hash1))
        .equals("blob abc123 Hello.txt\nblob def456 World.txt\n"));

    System.out.println("Same hash: " + hash1.equals(hash2));

} catch (IOException e) {
    e.printStackTrace();
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


//elle add writeInIndex

public static void writeInIndex(String filename) {
    try {

        //creates array list
        Path indexPath = Paths.get("git/index");
        List<String> lines = new ArrayList<>();
        
        // put lines into array list
        lines = Files.readAllLines(indexPath);
        

        String sha1Hash = FileHasher.hashFile(filename);
        String ent = sha1Hash + " " + filename;//new line/file situation
        boolean updated = false;

        //check if already exists
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(filename)) {
                //if alrady exits updaye it
                String[] parts = lines.get(i).split(" ");
                String x = parts[1]; //last element of the array
                if (x.equals(filename)) {
                    lines.set(i, ent);
                    updated = true;
                }
            }
        }
        //if doesnt alrewady exit add it
        if (!updated) {
            lines.add(ent);
        }
        //write it in
        Files.write(indexPath, lines);

    } catch (IOException e) {
        e.printStackTrace();
    }

}
    
public static String getDirPath(String path) {
    int slash = path.lastIndexOf("/");

    if (slash == -1) {
        return "";
    }

    return path.substring(0, slash);
}

public String createTree(List<String> workingList, String dirPath)
        throws IOException {

    StringBuilder content = new StringBuilder();

    for (String line : workingList) {
        //splits up and gets diffrent parts of the line
        String[] parts = line.split(" ", 3);
        String type = parts[0];
        String hash = parts[1];
        String path = parts[2];
        Path currentPath = Paths.get(path); 

        
        //checks if same
        if (getDirPath(path).equals(dirPath)) {
            String name = path.substring(path.lastIndexOf("/") + 1);
            content.append(type + " " + hash + " " + name + "\n");
        }
    } 
    File tempFile = new File("temp");
    tempFile.createNewFile();
    try (FileWriter tempWriter = new FileWriter(tempFile)) {
        tempWriter.write(content.toString());
    }
//creates the file and saves it under the hash
    String treeHash = FileHasher.hashFile(tempFile.getPath());

    File treeFile = new File("git/objects/" + treeHash);

    try (FileWriter writer = new FileWriter(treeFile)) {
        writer.write(content.toString());
    }

    return treeHash;

        // FileWriter tempWriter = new FileWriter(tempFile);
        // tempWriter.write(content);
        // tempWriter.close();
        // createBlob(tempFile.getPath());
        // String treeHash = FileHasher.hashFile(tempFile.getPath());
        // return treeHash;
}




    public static String deepestdir(List<String> workingList){
        return"";



    }
    public static void sort(){

    }
        //for my edits: duplicats work
        //It reades multiple lines
        //however I had to fix the index resetting


    }


    


