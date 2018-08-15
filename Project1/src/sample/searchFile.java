package sample;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.*;

public class searchFile implements Runnable {
    private File folder;
    private String extention;
    private String text;
    private TreeView<String> filetree;
    private TreeItem<String> root;

    public searchFile(File folder, String extention, String text, TreeView<String> filetree){
        this.folder=folder;
        this.extention=extention;
        this.text=text;
        this.filetree=filetree;
    }

    private void searchAllFileExtention(File curFolder){
        try {
            File[] folderEntries = curFolder.listFiles();
            if (folderEntries != null) {
                for (File entry : folderEntries) {
                    if (entry.isDirectory()) {
                        searchAllFileExtention(entry);
                        continue;
                    }
                    if (entry.getName().endsWith("." + extention)) {
                        searchAllText(entry.getPath());
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void searchAllText(String path) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String strLine;
        while ((strLine = br.readLine()) != null){
            if(strLine.contains(text)){
                TreeItem<String> current = root;
                for (String component : path.split("\\\\")) {
                    current = getOrCreateChild(current, component);
                }
                break;
            }
        }
    }

    private TreeItem<String> getOrCreateChild(TreeItem<String> parent, String value) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (value.equals(child.getValue())) {
                return child;
            }
        }
        TreeItem<String> newChild = new TreeItem<String>(value);
        parent.getChildren().add(newChild);
        return newChild;
    }

    public void run() {
        root = new TreeItem<>("PC");
        filetree.setRoot(root);
        searchAllFileExtention(folder);
    }
}
