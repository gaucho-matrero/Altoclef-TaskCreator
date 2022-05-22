package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private final JSONObject file;
    private boolean showWarning;

    public AltoFrame() {
        file = new JSONObject();
        showWarning = true;
        addMenu();
        setContentPane(mainPanel);
        setTitle("Altoclef-TaskCreator");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addActionListeners();
        setVisible(true);
        displayWarning();
    }

    /**
     * Create an instance of AltoFrame with an already existing CustomTasks.json
     * file
     *
     * @param file the path to an existing JSON file. Name is irrelevant.
     */
    public AltoFrame(JSONObject file) {
        this.file = file;
        showWarning = false;
        this.setLocationRelativeTo(null);
        addMenu();
        setContentPane(mainPanel);
        setTitle("Altoclef-TaskCreator");
        setLocationRelativeTo(null);
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addActionListeners();
        setVisible(true);
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

            }
        });
        btn_Compile.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    /* TODO Think about long term implications of making this private. Who is
     *   acutally going to handle the JSON files. Should FRAME be the one to
     * complain
     *   If there is a problem
     * */
    private void displayWarning() {
        AltoJsonWarning warningMessage = new AltoJsonWarning();
        warningMessage.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./img/warning.png");
        warningMessage.setIconImage(icon);
        warningMessage.setTitle("Warning: Cannot load JSON file");
        //TODO get this fixed. It displays the warning wrong.
        warningMessage.setL_warning_text("Altoclef-TaskCreator was unable to find or unable to load any JSONfiles. Changes will be saved to a new JSON file");
        warningMessage.setSize(450, 140);
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
        this.setJMenuBar(topMenu);
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
        mainPanel.setLayout(new GridLayoutManager(17, 5, new Insets(0, 8, 4,
                0), -1, -1));
        l_prefix = new JLabel();
        l_prefix.setText("Prefix");
        mainPanel.add(l_prefix, new GridConstraints(1, 0, 2, 3,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        tf_prefix = new JTextField();
        tf_prefix.setText("custom2");
        mainPanel.add(tf_prefix, new GridConstraints(3, 0, 3, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150,
                -1), null, 1, false));
        table_tasks = new JTable();
        mainPanel.add(table_tasks, new GridConstraints(1, 3, 15, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150
                , 50), null, 0, false));
        l_v_label = new JLabel();
        l_v_label.setBackground(new Color(-9346490));
        l_v_label.setForeground(new Color(-4500880));
        l_v_label.setText("v0.5");
        mainPanel.add(l_v_label, new GridConstraints(16, 0, 1, 3,
                GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_newTask = new JButton();
        btn_newTask.setText("New Task");
        mainPanel.add(btn_newTask, new GridConstraints(10, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_delTask = new JButton();
        btn_delTask.setText("Delete Task");
        mainPanel.add(btn_delTask, new GridConstraints(12, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_Compile = new JButton();
        btn_Compile.setText("Compile");
        mainPanel.add(btn_Compile, new GridConstraints(16, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(15, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        l_shareableString = new JLabel();
        l_shareableString.setText("");
        mainPanel.add(l_shareableString, new GridConstraints(15, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(10, 4, 6, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0,
                false));
        l_task_description_header = new JLabel();
        l_task_description_header.setEnabled(true);
        l_task_description_header.setText("");
        mainPanel.add(l_task_description_header, new GridConstraints(6, 4, 3,
                1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        l_task_description = new JLabel();
        l_task_description.setText("");
        mainPanel.add(l_task_description, new GridConstraints(9, 4, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(8, 8, 0, 0),
                -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 3, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btn_genShareString = new JButton();
        btn_genShareString.setText("Create Shared String");
        mainPanel.add(btn_genShareString, new GridConstraints(13, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_edit_task = new JButton();
        btn_edit_task.setText("Edit Task");
        mainPanel.add(btn_edit_task, new GridConstraints(11, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}