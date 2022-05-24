package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import james.altoclef.taskcreator.utils.JSONManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AltoFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField tf_prefix;
    private JTable table_tasks;
    private JButton btn_Compile;
    private JButton btn_delTask;
    private JButton btn_newTask;
    private JButton btn_genShareString;
    private JLabel l_shareableString;
    private JLabel l_prefix;
    private JLabel l_v_label;
    private JLabel l_task_description_header;
    private JLabel l_task_description;
    private JButton btn_edit_task;
    private JSONObject file;
    private JSONManager manager;
    private boolean needToSave = false;
    public AltoFrame() {
        file = new JSONObject();
        addMenu();
        setContentPane(mainPanel);
        setTitle("Altoclef-TaskCreator");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addActionListeners();
        setVisible(true);
    }

    /**
     * Create an instance of AltoFrame with an already existing CustomTasks.json
     * file
     *
     * @param filename the path to an existing JSON file. Name is irrelevant.
     */
    public AltoFrame(String filename) throws IOException, ParseException {
        {
            this.setLocationRelativeTo(null);
            addMenu();
            setContentPane(mainPanel);
            setTitle("Altoclef-TaskCreator");
            setSize(750, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            addActionListeners();
            setLocationRelativeTo(null);
            setVisible(true);
            table_tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } //init
        try {
            manager = new JSONManager(filename);
            loadJsonFile(manager.getFile());

        } catch (Exception e) {
            manager = new JSONManager();
            displayWarning("Altoclef-TaskCreator was unable to find or unable to load any JSON files. Changes will be saved to a new JSON file");
        } //load default file

        refreshTable();
    }

    private void addActionListeners() {
        btn_newTask.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTaskFrame ntf = new NewTaskFrame(file); //TODO Make this work
            }
        });
        btn_edit_task.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btn_delTask.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = table_tasks.getSelectedRow();
                JSONArray arg = file.getJSONArray("custom-tasks");
                arg.remove(index);
                refreshTable();
                inform(true);
            }
        });
        btn_genShareString.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String shareable = manager.compressToString();
                    l_shareableString.setText("Copied to clipboard!");
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(shareable);
                    clipboard.setContents(selection, selection);
                } catch (UnsupportedEncodingException ex) {
                    displayWarning("Unable to work with this encoding type");
                }
            }
        });

        btn_Compile.addActionListener(new ActionListener() {
            /**
             * Invoked when compile button is pressed.
             * @apiNote Although the user gets to specify the file name, Altoclef will
             * only load it if it has the name "CustomTasks.json". Having a flexible filenaming system allows
             * users to save different configurations and manually load them. We may modify altoclef's behavior in the future
             * to enable loading multiple file names.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Save", FileDialog.SAVE);
                fd.setTitle("Choose a  save location");
                //fd.setFile("CustomTasks.json"); uncomment if we want to force the file name
                fd.setVisible(true);
                try {
                    JSONObject toWrite = file;
                    FileWriter filew = new FileWriter(fd.getFile());
                    toWrite.write(filew);
                    filew.close();
                    manager = new JSONManager(fd.getFile());
                    inform(false);
                } catch (IOException | JSONException | ParseException ignored) {
                   displayWarning("Failed to load file");
                }
                loadJsonFile(manager.getFile());
            } /* TODO
                    1. Cancel saving if window is closed, but no save button is pressed.
                    2. Remove the ".json.json" problem, and set .json as the default file extension.
            */
        });
    }

    /**
     * toggles unsaved changes notifier
     * @param changes_detected whether changes have been made or not
     */
    private void inform(boolean changes_detected) {
        needToSave = changes_detected;
        if(changes_detected)
            setTitle(getTitle()+" *unsaved changes detected*");
        else
            setTitle("Altoclef-TaskCreator " + manager.getFileName()); //only trigger after compile
    }

    /**
     * Displays warning text.
     */
    private void displayWarning(String message) {
        AltoJsonWarning warningMessage = new AltoJsonWarning();
        warningMessage.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./img/warning.png");
        warningMessage.setIconImage(icon);
        warningMessage.setTitle("Warning: Cannot load JSON file");
        warningMessage.setL_warning_text("<html><center><p style=\"width:300px\">" + message + "</p></center></html>");
        warningMessage.setSize(450, 200);
        warningMessage.setLocationRelativeTo(null);
        warningMessage.setVisible(true);
    }

    public void addMenu() {
        JMenuBar topMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenu_newFile = new JMenuItem("New");
        JMenu fileMenu_subMenuOpen = new JMenu("Open");
        JMenuItem fileMenu_subMenuOpen_fromFileSystem = new JMenuItem("From File System");
        JMenuItem fileMenu_subMenuOpen_fromString = new JMenuItem("From String");
        fileMenu.add(fileMenu_newFile);
        fileMenu_subMenuOpen.add(fileMenu_subMenuOpen_fromFileSystem);
        fileMenu_subMenuOpen.add(fileMenu_subMenuOpen_fromString);
        fileMenu.add(fileMenu_subMenuOpen);
        JMenu options = new JMenu("Tools");
        JMenuItem options_viewUsageGuide = new JMenuItem("Usage Guide");
        options.add(options_viewUsageGuide);
        topMenu.add(fileMenu);
        topMenu.add(options);

        { //action listeners
            fileMenu_subMenuOpen_fromFileSystem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        FileDialog fd = new FileDialog(new JFrame(), "Select a file", FileDialog.LOAD);
                        fd.setVisible(true);
                        exploreForJson(fd);
                    } catch (Exception ignored) {
                        displayWarning("Unable to load file");
                    }
                }
            });
            fileMenu_newFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(needToSave) {
                        AltoJsonWarning warningMessage = new AltoJsonWarning();
                        warningMessage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        Image icon = Toolkit.getDefaultToolkit().getImage("./img/warning.png");
                        warningMessage.setIconImage(icon);
                        warningMessage.setTitle("Warning: Unsaved changes");
                        warningMessage.setL_warning_text("<html><center><p style=\"width:300px\">" + "Unsaved changes will be lost" + "</p></center></html>");
                        warningMessage.setSize(450, 200);
                        warningMessage.setLocationRelativeTo(null);
                        warningMessage.setVisible(true);

                        if (warningMessage.OKPressed()) {
                            manager = new JSONManager();
                            file = manager.getFile();
                            setTitle("Altoclef-TaskCreator -- new file");
                            refreshTable();
                            l_shareableString.setText("");
                            inform(true);
                        }
                    }else {
                        manager = new JSONManager();
                        file = manager.getFile();
                        setTitle("Altoclef-TaskCreator -- new file");
                        refreshTable();
                        l_shareableString.setText("");
                        inform(true);
                    }
                }
            });
            fileMenu_subMenuOpen_fromString.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StringImporter importer = new StringImporter();
                    importer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    importer.setTitle("String importer");
                    importer.setSize(450, 200);
                    importer.setLocationRelativeTo(null);
                    importer.setVisible(true);

                    JSONObject workingFile = importer.getDecompresssed();
                    if (workingFile != null) {
                        if (workingFile.toString().equals("")) {
                            displayWarning("String import was un-succesful");
                        } else {
                            loadJsonFile(workingFile);
                            setTitle("");
                        }
                    }

                }
            });
        }

        this.setJMenuBar(topMenu);

    }

    /**
     * Used to refresh everything
     */
    private void refresh() {
        l_shareableString.setText("");
        setTitle("");
        refreshTable();
    }

    private void exploreForJson(FileDialog fd) throws IOException, ParseException {
        manager = new JSONManager(fd.getFile());
        file = manager.getFile();
        setTitle("Altoclef-TaskCreator -- " + manager.getFileName());
        l_shareableString.setText("");
        refreshTable();
    }

    private void loadJsonFile(JSONObject file) {
        manager.setFile(file);
        this.file = manager.getFile();
        setTitle("Altoclef-TaskCreator -- " + manager.getFileName());
        l_shareableString.setText("");
        refreshTable();
    }

    /**
     * Refreshes data displayed on the table
     *
     * @implNote This implemnentation has only one column "Task-Title"
     * @implSpec This implementation is read only and does not modify how data
     * is acquired
     */
    private void refreshTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Task Title"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table_tasks.setModel(model); //used to clear the table

        try {
            if (file == null) {
                displayWarning("Unable to load json file");
                throw new JSONException("Unable to load json file");
            }
            for (String title : manager.getTaskNames()) {
                model.addRow(new String[]{title});
            }
            table_tasks.setModel(model);
            table_tasks.setVisible(true);


        } catch (Exception ignored) {

        }
    }

}