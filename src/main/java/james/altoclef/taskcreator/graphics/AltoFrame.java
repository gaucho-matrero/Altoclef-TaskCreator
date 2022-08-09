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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;

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
    private JLabel l_release_title;
    private JLabel l_space;
    private JLabel l_custom_preview;
    private JButton btn_edit;
    private JLabel l_task_desc_title;
    private JLabel l_task_desc_contents;
    private JPanel panel_task_description;
    private JTable table_task_desc;
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
            addMenu(); //this must come first
            setContentPane(mainPanel);
            setTitle("Altoclef-TaskCreator");
            setSize(750, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            addListeners();
            setLocationRelativeTo(null);
            table_tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setVisible(true); //this must always be last
        } //init
        try {
            manager = new JSONManager(filename);
            loadJsonFile(manager.getFile());

        } catch (Exception e) {
            manager = new JSONManager();
            displayWarning("Altoclef-TaskCreator was unable to find or unable to load any JSON files. Changes will be saved to a new JSON file");
            file = manager.getFile();
            setTitle("Altoclef-TaskCreator -- new file");
            refreshTable();
            l_shareableString.setText("");
            inform(true);
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
                if (e.getKeyChar() == '\u001B') {
                    table_tasks.clearSelection();
                    btn_delTask.setEnabled(false);
                    btn_edit.setEnabled(false);
                    l_task_desc_title.setText("");
                    l_task_desc_contents.setText("");
                    panel_task_description.setVisible(false);

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
                if (table_tasks.getSelectedRow() != -1) {
                    btn_delTask.setEnabled(true);
                    btn_edit.setEnabled(true);
                    panel_task_description.setVisible(true);
                    l_task_desc_title.setText("Name: \"" + file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getString("name") + "\"");
                    l_task_desc_contents.setText("Description: " + file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getString("description"));
                    DefaultTableModel m = new DefaultTableModel(new String[]{"task type", "parameters"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //all cells false
                            table_task_desc.setRowSelectionAllowed(false);
                            table_task_desc.setFocusable(false);
                            return false;
                        }
                    };
                    Object[] tasks = file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getJSONArray("tasks").toList().toArray();
                    for (int i = 0; i < tasks.length; i++) {
                        String type = file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getJSONArray("tasks").getJSONObject(i).getString("command");
                        Object[] shortdesc_list;
                        String description_short;
                        try {
                            shortdesc_list = (Object[]) file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getJSONArray("tasks").getJSONObject(i).getJSONArray("parameters").toList().toArray();
                            description_short = shortdesc_list[0].toString() + (shortdesc_list.length > 1 ? " + ... " + shortdesc_list.length + " more" : "");
                        } catch (Exception wasEdited) {
                            shortdesc_list = (Object[]) file.getJSONArray("customTasks").getJSONObject(table_tasks.getSelectedRow()).getJSONArray("tasks").getJSONObject(i).get("parameters");
                            description_short = (String) Array.get(shortdesc_list[0], 0) + (shortdesc_list.length > 1 ? " + ... " + shortdesc_list.length + " more" : "");
                        }
                        //TODO make it look exactly the way it does in the other view. Cut it off after 15 tasks
                        m.addRow(new String[]{type, description_short});
                    }
                    table_task_desc.setModel(m);
                    table_task_desc.setVisible(true);
                }
            }
        });
        btn_edit.addActionListener(e -> {
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
                JSONObject toAdd = nTF.write();
                if (toAdd != null) {
                    array_on_file.put(toAdd);
                } else {
                    AltoJsonWarning warnUserOfBadAction = new AltoJsonWarning("Bad Action", "you cannot save a custom task with no sub actions");
                }
                inform(true);
                refreshTable();
            } catch (Exception ignored) {
                //we don't do anything if the table was not modified
            }
        }); //TODO Load file from the same location as it was saved
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
                    JSONObject toAdd = nTF.write();
                    if (toAdd != null) {
                        array_on_file.put(toAdd);
                        inform(true);
                    }
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
        btn_genShareString.addActionListener(e -> {
            try {
                String shareable = manager.compressToString();
                l_shareableString.setText("Copied to clipboard!");
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(shareable);
                clipboard.setContents(selection, selection);
            } catch (UnsupportedEncodingException ex) {
                displayWarning("Unable to work with this encoding type");
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
                    manager = new JSONManager(fd.getDirectory() + newFilename);
                    inform(false);
                } catch (IOException | JSONException | ParseException | NullPointerException ex) {
                    if (!(ex instanceof NullPointerException))
                        displayWarning("Failed to load file");
                }
                loadJsonFile(manager.getFile());
            }
        });
        initComponents();
    }

    private void initComponents() {
        panel_task_description.setVisible(false);
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
            options_viewUsageGuide.addActionListener(e -> {
                displayWarning("Usage guide currently under construction. Message James Green on discord if you have any questions.");
            });
        }

        this.setJMenuBar(topMenu);

    } //TODO Only display unsaved changes if file was changed. Currently, opening a file causes this to appear

    private void exploreForJson(FileDialog fd) throws IOException, ParseException {
        manager = new JSONManager(fd.getDirectory() + fd.getFile());
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
        boolean entrySelected = table_tasks.getSelectedRow() != -1;
        btn_delTask.setEnabled(entrySelected);
        btn_edit.setEnabled(entrySelected);
        //clear the table on reset.
        /*        table_task_desc.setModel(new DefaultTableModel(new
        String[]{"action type","action parameters"},0){
         *//**
         * Returns true regardless of parameter values.
         *
         * @param row    the row whose value is to be
         *               queried
         * @param column the column whose value is to be
         *               queried
         * @return true
         *
         * @see #setValueAt
         *//*
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });*/
        panel_task_description.setVisible(false);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT
     * edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(18, 5, new Insets(8, 8, 8,
                8), -1, -1));
        mainPanel.setForeground(new Color(-1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        l_prefix = new JLabel();
        l_prefix.setText("Prefix");
        mainPanel.add(l_prefix, new GridConstraints(2, 0, 2, 3,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        tf_prefix = new JTextField();
        tf_prefix.setEditable(false);
        tf_prefix.setEnabled(false);
        tf_prefix.setText("custom");
        mainPanel.add(tf_prefix, new GridConstraints(4, 0, 3, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150,
                -1), null, 1, false));
        table_tasks = new JTable();
        mainPanel.add(table_tasks, new GridConstraints(2, 3, 15, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150
                , 50), null, 0, false));
        l_v_label = new JLabel();
        l_v_label.setBackground(new Color(-9346490));
        l_v_label.setForeground(new Color(-4500880));
        l_v_label.setText("v1.2");
        mainPanel.add(l_v_label, new GridConstraints(17, 0, 1, 1,
                GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_newTask = new JButton();
        btn_newTask.setHideActionText(false);
        btn_newTask.setText("New Task");
        mainPanel.add(btn_newTask, new GridConstraints(11, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_delTask = new JButton();
        btn_delTask.setText("Delete Task");
        mainPanel.add(btn_delTask, new GridConstraints(13, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_Compile = new JButton();
        btn_Compile.setText("Compile");
        mainPanel.add(btn_Compile, new GridConstraints(17, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(16, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        l_shareableString = new JLabel();
        l_shareableString.setText("");
        mainPanel.add(l_shareableString, new GridConstraints(16, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(11, 4, 6, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0,
                false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(8, 8, 0, 0),
                -1, -1));
        panel1.setBackground(new Color(-14194321));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 5,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        l_release_title = new JLabel();
        l_release_title.setForeground(new Color(-1));
        l_release_title.setText("Functional Release");
        l_release_title.setVerticalAlignment(0);
        panel1.add(l_release_title, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        l_space = new JLabel();
        l_space.setText("");
        panel1.add(l_space, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_genShareString = new JButton();
        btn_genShareString.setText("Create Shared String");
        mainPanel.add(btn_genShareString, new GridConstraints(14, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        l_custom_preview = new JLabel();
        l_custom_preview.setText("Custom Tasks");
        mainPanel.add(l_custom_preview, new GridConstraints(1, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_edit = new JButton();
        btn_edit.setText("Edit");
        mainPanel.add(btn_edit, new GridConstraints(12, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel_task_description = new JPanel();
        panel_task_description.setLayout(new GridLayoutManager(3, 1,
                new Insets(8, 8, 8, 8), -1, -1));
        panel_task_description.setBackground(new Color(-1246977));
        panel_task_description.setForeground(new Color(-1));
        mainPanel.add(panel_task_description, new GridConstraints(3, 4, 8, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        l_task_desc_title = new JLabel();
        l_task_desc_title.setForeground(new Color(-16777216));
        l_task_desc_title.setText("");
        panel_task_description.add(l_task_desc_title, new GridConstraints(0,
                0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        l_task_desc_contents = new JLabel();
        l_task_desc_contents.setForeground(new Color(-16777216));
        l_task_desc_contents.setText("");
        panel_task_description.add(l_task_desc_contents,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0
                        , false));
        table_task_desc = new JTable();
        panel_task_description.add(table_task_desc, new GridConstraints(2, 0,
                1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150
                , 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}