package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.utils.JSONManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AltoFrame extends JFrame {
    public static final int UNLOCK_ON_CLOSE = 99;
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
    private JLabel l_release_title;
    private JLabel l_space;
    private JLabel l_custom_preview;
    private JButton btn_edit;
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
        addListeners();
        setVisible(true);
    }

    /**
     * Create an instance of AltoFrame with an already existing CustomTasks.json
     * file
     *
     * @param filename the path to an existing JSON file. Name is irrelevant.
     */
    public AltoFrame(String filename) {
        {
            this.setLocationRelativeTo(null);
            addMenu();
            setContentPane(mainPanel);
            setTitle("Altoclef-TaskCreator");
            setSize(750, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            addListeners();
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

    private void addListeners() {
        table_tasks.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed. This event
             * occurs when a key press is followed by a key
             * release.
             *
             * @param e
             */
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(e.getKeyChar()=='\u001B'){
                    table_tasks.clearSelection();
                    btn_delTask.setEnabled(false);
                    btn_edit.setEnabled(false);
                }
            }
        });
        table_tasks.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(table_tasks.getSelectedRow()!=-1){
                    btn_delTask.setEnabled(true);
                    btn_edit.setEnabled(true);
                }
            }
        });
        btn_edit.addActionListener(e->{
            JSONArray array_on_file = new JSONArray();
            try {
                array_on_file = file.getJSONArray("customTasks");
            } catch (JSONException ignored) {
                file.put("prefix", tf_prefix.getText());
                file.put("customTasks", array_on_file);
            }
            NewTaskFrame nTF = new NewTaskFrame(file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()));
            try {
                array_on_file.remove(table_tasks.getSelectedRow());
                array_on_file.put(nTF.write());
                inform(true);
                refreshTable();
            } catch (Exception ignored) {
                //we don't do anything if the table was not modified
            }
        });
        btn_newTask.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                TODO
                    The result of the new task frame will be a new custom task.
                    file.getJSONarray[0] gets the custom tasks data
                    we add the results of new
                 */
                JSONArray array_on_file = new JSONArray();
                try {
                    array_on_file = file.getJSONArray("customTasks");
                } catch (JSONException ignored) {
                    file.put("prefix", tf_prefix.getText());
                    file.put("customTasks", array_on_file);
                }
                NewTaskFrame nTF = new NewTaskFrame();
                try {
                    array_on_file.put(nTF.write());
                    inform(true);
                    refreshTable();
                } catch (Exception ignored) {
                    //we don't do anything if the table was not modified
                }
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
                JSONArray arg = file.getJSONArray("customTasks");
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
                    String newFilename = fd.getFile().endsWith(".json") ? fd.getFile() : fd.getFile() + ".json";
                    JSONObject toWrite = file;
                    FileWriter filew = new FileWriter(fd.getDirectory() + newFilename);
                    toWrite.write(filew);
                    filew.close();
                    manager = new JSONManager(newFilename);
                    inform(false);
                } catch (IOException | JSONException | ParseException | NullPointerException ex) {
                    if (!(ex instanceof NullPointerException))
                        displayWarning("Failed to load file");
                }
                loadJsonFile(manager.getFile());
            }
        });
    }

    /**
     * toggles unsaved changes notifier
     *
     * @param changes_detected whether changes have been made or not
     */
    private void inform(boolean changes_detected) {
        if (needToSave != changes_detected) {
            needToSave = changes_detected;
            if (changes_detected)
                setTitle(getTitle() + " *unsaved changes detected*");
            else
                setTitle("Altoclef-TaskCreator " + manager.getFileName()); //only trigger after compile
        }
    }

    /**
     * Displays warning text.
     */ //TODO Refactor
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
                        inform(false);
                    } catch (Exception ignored) {
                        displayWarning("Unable to load file");
                    }
                }
            });
            fileMenu_newFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (needToSave) {
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
                    } else {
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

    } //TODO Only display unsaved changes if file was changed. Currently, opening a file causes this to appear

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

        btn_delTask.setEnabled(table_tasks.getSelectedRow() != -1);
    }

}