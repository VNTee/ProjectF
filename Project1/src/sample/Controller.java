package sample;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {
    public TextArea Content;
    private File choosenPath = null;
    public TextArea fileExtention = new TextArea();
    public TreeView fileTree = new TreeView();
    public TabPane myTabPane;
    public TextArea ourText = new TextArea();
    private String ourTextCurrent = null;
    public Tab plusTab;
    private List<String> fileInsideTheList;
    private List<Integer> indexOfLine = new ArrayList<Integer>();
    private int currentIndexOfLine = 0;
    private int scrollPositionLine = 0;
    private int indexFMWIL = 0;
    private boolean containMoreWords = false;
    private boolean containMoreWordsBack = false;
    private int lengthOfOurTextCurrent;
    private boolean srcDone = false;
    private boolean srcFirstTime = true;
    StringBuilder sb = new StringBuilder();
    private ListIterator<String> iter;
    private ListIterator<Integer>  iterIndexOfLine;
    static ExecutorService executor;

    private EventHandler<MouseEvent> mouseEventHandlerTree = this::handleMouseClickedTree;

    public void findFile(ActionEvent actionEvent) {
        if((!ourText.getText().equals(""))&&fileExtention.getText()!=null&&choosenPath!=null) {
            ourTextCurrent = ourText.getText();
            if(fileTree.getRoot()!=null) {
                fileTree.getRoot().getChildren().removeAll();
                fileTree.setRoot(null);
            }
            lengthOfOurTextCurrent = ourTextCurrent.length();
            Runnable myRun = new searchFile(choosenPath, fileExtention.getText(), ourTextCurrent, fileTree);
            try {
                executor.execute(myRun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openDirectory(ActionEvent actionEvent) {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            choosenPath = selectedDirectory;
        }
        System.out.println("We got starter path: " + choosenPath.getAbsolutePath());
    }

    public void plusNewTab(Event event) {
        if(plusTab.isSelected()) {
            try {
                Node node = FXMLLoader.load(getClass().getResource("newTab.fxml"));
                Tab addNewTab = new Tab();
                addNewTab.setContent(node);
                addNewTab.setClosable(true);
                addNewTab.setText("Вкладка");
                myTabPane.getTabs().add(addNewTab);
                myTabPane.getSelectionModel().select(addNewTab);
                addNewTab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        myTabPane.getSelectionModel().selectNext();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextWord(ActionEvent actionEvent) {
        if(indexOfLine.size()>currentIndexOfLine&& srcDone) {
            sb.setLength(0);
            iter = fileInsideTheList.listIterator(indexOfLine.get(currentIndexOfLine));
            iterIndexOfLine = indexOfLine.listIterator(currentIndexOfLine);
            int a = 0;
            int indexInLine = 0;
            Content.setText("");
            String m;
            while (iter.hasNext() && a < 100) {
                if(a==0) {
                    int backI;
                    int forwardI;
                        try {
                            backI = iterIndexOfLine.previous();
                            forwardI = iterIndexOfLine.next();
                            forwardI = iterIndexOfLine.next();
                        } catch (Exception e){
                                backI = 0;
                                forwardI = 0;
                        }
                    if(!iterIndexOfLine.hasPrevious() ||backI!=forwardI) {
                        m=iter.next();
                        sb.append(m+"\n");
                        a = a + 1;
                        containMoreWords = false;
                        indexInLine = m.indexOf(ourTextCurrent);
                        indexFMWIL=indexInLine+lengthOfOurTextCurrent;
                        continue;
                    }
                    else {
                        m = iter.next();
                        sb.append(m+"\n");
                        a = a + 1;
                        if(containMoreWords)
                            indexFMWIL=indexFMWIL+lengthOfOurTextCurrent;
                        m=m.substring(indexFMWIL);
                        indexFMWIL=indexFMWIL+m.indexOf(ourTextCurrent);
                        containMoreWords = true;
                        continue;
                         }
                             }
                     m = iter.next();
                       sb.append(m+"\n");
                     a = a + 1;
            }
            Content.setText(sb.toString());
            if(containMoreWords)
            Content.selectRange(indexFMWIL,indexFMWIL+lengthOfOurTextCurrent);
            else
            Content.selectRange(indexInLine,indexInLine+lengthOfOurTextCurrent);
            scrollPositionLine = indexOfLine.get(currentIndexOfLine)+a;
            currentIndexOfLine = currentIndexOfLine + 1;
        }
        System.out.println(scrollPositionLine);
    }

    public void backWord(ActionEvent actionEvent) {
        if (currentIndexOfLine!=0){
            if (currentIndexOfLine >= 0 && (currentIndexOfLine - 1) != 0 && srcDone) {
                currentIndexOfLine = currentIndexOfLine - 1;
                sb.setLength(0);
                iter = fileInsideTheList.listIterator(indexOfLine.get(currentIndexOfLine - 1));
                iterIndexOfLine = indexOfLine.listIterator(currentIndexOfLine);
                int a = 0;
                int indexInLine = 0;
                Content.setText("");
                String m;
                while (iter.hasNext() && a < 100) {
                    if (a == 0) {
                        int backI;
                        int forwardI;
                        try {
                            forwardI = iterIndexOfLine.next();
                            backI = iterIndexOfLine.previous();
                            backI = iterIndexOfLine.previous();
                        } catch (Exception e) {
                            backI = 0;
                            forwardI = 0;
                        }
                        if (!iterIndexOfLine.hasNext() || backI != forwardI) {
                            m = iter.next();
                            sb.append(m + "\n");
                            a = a + 1;
                            indexInLine = m.lastIndexOf(ourTextCurrent);
                            indexFMWIL = indexInLine - lengthOfOurTextCurrent;
                            containMoreWordsBack = false;
                            continue;
                        } else {
                            m = iter.next();
                            sb.append(m + "\n");
                            a = a + 1;
                            m = m.substring(0, indexFMWIL);
                            indexFMWIL = m.lastIndexOf(ourTextCurrent);
                            containMoreWordsBack = true;
                            continue;
                        }
                    }
                    m = iter.next();
                    sb.append(m + "\n");
                    a = a + 1;
                }
                Content.setText(sb.toString());
                if (containMoreWordsBack)
                    Content.selectRange(indexFMWIL, indexFMWIL + lengthOfOurTextCurrent);
                else
                    Content.selectRange(indexInLine, indexInLine + lengthOfOurTextCurrent);
                scrollPositionLine = indexOfLine.get(currentIndexOfLine) + a;
            }
        }
        System.out.println(scrollPositionLine);
    }

    private void handleMouseClickedTree(MouseEvent event){
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                TreeItem<String> currentItem = (TreeItem<String>) fileTree.getSelectionModel().getSelectedItem();
                if (currentItem.isLeaf() &&(srcFirstTime || srcDone)) {
                    sb.setLength(0);
                    scrollPositionLine=0;
                    srcFirstTime = false;
                    srcDone = false;
                    String pathOfNode = currentItem.getValue();
                    while (!currentItem.getParent().getValue().equals("PC")) {
                        pathOfNode = currentItem.getParent().getValue() + "\\" + pathOfNode;
                        currentItem = currentItem.getParent();
                    }
                    try {
                        try {
                            fileInsideTheList = Files.lines(Paths.get(pathOfNode), StandardCharsets.UTF_8).collect(Collectors.toList());
                        } catch (Exception e){
                                Files.lines(Paths.get(pathOfNode), StandardCharsets.UTF_16).collect(Collectors.toList());
                        }
                        currentIndexOfLine = 0;
                        String s;
                        int l;
                        indexOfLine.clear();
                        iter = fileInsideTheList.listIterator();
                        while (iter.hasNext()){
                            l = iter.nextIndex();
                            s = iter.next();
                            while(s.indexOf(ourTextCurrent)!=-1) {
                                s=s.substring(s.indexOf(ourTextCurrent)+lengthOfOurTextCurrent);
                                indexOfLine.add(l);
                            }
                        }
                        iter = fileInsideTheList.listIterator();
                        int a = 0;
                        while (iter.hasNext() && a < 100) {
                            sb.append(iter.next()+"\n");
                           a = a + 1;
                        }
                        Content.setText("");
                        Content.setText(sb.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    srcDone = true;
                    System.out.println(scrollPositionLine);
                }
            }
        }

    public void scrollUp(ActionEvent actionEvent) {
        if(srcDone) {
            iter = fileInsideTheList.listIterator(scrollPositionLine);
            System.out.println(scrollPositionLine);
            if (iter.hasPrevious()) {
                sb.setLength(0);
                int b = iter.previousIndex() + 1;
                System.out.println(b);
                if (iter.previousIndex() >= 100) {
                    iter = fileInsideTheList.listIterator(scrollPositionLine - 100);
                    int a = 0;
                    Content.setText("");
                    while (iter.hasNext() && a < 100) {
                        sb.append(iter.next() + "\n");
                        a = a + 1;
                    }
                    Content.setText(sb.toString());
                    scrollPositionLine = scrollPositionLine - a;
                } else {
                    iter = fileInsideTheList.listIterator(0);
                    int a = 0;
                    Content.setText("");
                    while (iter.hasNext() && a < b) {
                        sb.append(iter.next() + "\n");
                        a = a + 1;
                    }
                    Content.setText(sb.toString());
                    sb.setLength(0);
                    scrollPositionLine = scrollPositionLine - a;
                }
            }
            Content.setScrollTop(Double.MAX_VALUE);
            System.out.println(scrollPositionLine);
        }
    }

    public void scrollDown(ActionEvent actionEvent) {
        if(srcDone) {
            iter = fileInsideTheList.listIterator(scrollPositionLine);
            if (iter.hasNext()) {
                sb.setLength(0);
                int a = 0;
                Content.setText("");
                while (iter.hasNext() && a < 100) {
                    sb.append(iter.next() + "\n");
                    a = a + 1;
                }
                Content.setText(sb.toString());
                scrollPositionLine = scrollPositionLine + a;
            }
            System.out.println(scrollPositionLine);
        }
    }

    public void highlightAll(ActionEvent actionEvent) {
        if(srcDone){

        }
    }

    @FXML
    public void initialize () {
        fileTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandlerTree);
        fileExtention.setText("log");
        ourText.setText("");
        executor = Executors.newFixedThreadPool(10);
    }
}
