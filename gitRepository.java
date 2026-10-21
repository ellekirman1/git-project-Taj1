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
        //test truee



        //tests 3.2, 3.3 

List<String> workingList = new ArrayList<>();
workingList.add("blob abc123 myProgram/docs/Hello.txt");
workingList.add("blob def456 myProgram/docs/World.txt");
workingList.add("blob ghi789 myProgram/README.md");

try {
    //3.2
    String treehash = git.createTree(workingList, "myProgram/docs");
    System.out.println("Tree Hash: " + treehash);
    System.out.println(Files.readString(Paths.get("git/objects/" + treehash)));

    //3.3
    System.out.println(git.createTreefromIndex());
    Path tree = Paths.get("git/objects/" + git.createTreefromIndex());

    if (Files.exists(tree)) {
        System.out.println("yayyyy");
    } else {
        System.out.println("failed");
    }

} catch (IOException e) {
    System.out.println("Test Failed: " + e.getMessage());
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


//3.2
    
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
        //Path currentPath = Paths.get(path); 

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




//3.3

public String createTreefromIndex() throws IOException {
    //made using lots of helpers. while loop - not recursive. goes through working list.
    List<String> workingList = makeWorkingList();
    String x = "";
    while (!workingList.isEmpty()) {
        //make tree removes level each time
        x = maketree(workingList, deepestDir(workingList));
    }
    return x;
}


    public String deepestDir(List<String> workingList) {
    String deepestDir = "";
    int maxDepth = -1;//-1 gives no issues
    for (String line : workingList) {
        //goes through list and finds path and directory
        String path = line.split(" ", 3)[2];
        String dir = getDirPath(path);

        //finds its depth via looking at /
        int depth = 0;
        if (!dir.equals("")) {
            depth = dir.split("/").length;
        }
    //finds and saves deepest directory
        if (depth > maxDepth) {
            maxDepth = depth;
            deepestDir = dir;
        }
    }
    return deepestDir;
}

public String maketree(List<String> workingList, String dirPath)
        throws IOException {
            //creates the tree and removes childrent 

    sort(workingList);
    String hash = createTree(workingList, dirPath);

    for (int i = workingList.size() - 1; i >= 0; i--) {//goes through backwards
        String path = workingList.get(i).split(" ", 3)[2];//gets path in middle
        if (getDirPath(path).equals(dirPath)) {
            workingList.remove(i);
        }//gets rid of children
    }
    if (!dirPath.equals("")) {
        workingList.add("tree " + hash + " " + dirPath);
    }

    return hash;
}

public List<String> makeWorkingList() throws IOException {
    List<String> workingList = new ArrayList<>();
    List<String> lines = Files.readAllLines(Paths.get("git/index"));

    for (String line : lines) {
        if (!line.isBlank()) {
            workingList.add("blob " + line);
        }
    }
    sort(workingList);//below
    return workingList;
    //this method was made in 3.3. 3.2 would have been easier if already made. However I made a working list in the tester, not in a method.
    //this is a better way to do this i belive. it uses other helpers made.
}
public void sort(List<String> workingList) {   
    
    for (int i = 0; i < workingList.size(); i++) { //most likly inefficent way to compare. however ensures sort.    
        for (int j = i + 1; j < workingList.size(); j++) {

            //gets paths (middle section of entry)
            String path1 = workingList.get(i).split(" ", 3)[2];
            String path2 = workingList.get(j).split(" ", 3)[2];

            if (path1.compareTo(path2) > 0) {//sorts by path. capital letters?
                String temp = workingList.get(i);
                workingList.set(i, workingList.get(j));
                workingList.set(j, temp);//switches if goes into if statment
            }
        }
    }
}


    }


    


