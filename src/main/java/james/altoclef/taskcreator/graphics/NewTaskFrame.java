package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import james.altoclef.taskcreator.customSubTask;
import james.altoclef.taskcreator.interfaces.Key;
import james.altoclef.taskcreator.writeableTask;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewTaskFrame extends JDialog {
    private JTable subtask_table;
    private JComboBox<String> commandBox;
    private JLabel l_command;
    private JButton btn_add_sub_task;
    private JLabel l_param;
    private JButton btn_Remove;
    private JButton btn_save;
    private JButton btn_Cancel;
    private JPanel main_panel;
    private JTextField tf_name;
    private JTextField tf_desc;
    private JButton btn_clearAll;
    private JButton btn_copy;
    private JLabel l_task_preview;

    private final writeableTask taskToWrite = new writeableTask();
    private final List<customSubTask> subTaskList;

    public NewTaskFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setContentPane(main_panel);

        setSize(950, 520);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btn_save);
        subTaskList = new ArrayList<customSubTask>();
        initComponents();
        setVisible(true);//must always be last
    }

    private void initComponents() {

        //TODO combo box
        commandBox.addItem("get");
        commandBox.addItem("goto");
        commandBox.addItem("punk");

        subtask_table.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && subtask_table.getSelectedRow() != -1) {
                    openSubtaskMenu();
                } else {
                    if (subtask_table.getSelectedRow() != -1) {
                        btn_add_sub_task.setText("Edit");
                        btn_copy.setEnabled(true);
                        btn_Remove.setEnabled(true);
                    }
                }
            }


        });
        btn_add_sub_task.addActionListener(e -> {
            openSubtaskMenu();
        });
        btn_Remove.addActionListener(e -> {
            if (subtask_table.getSelectedRow() != -1) {
                subTaskList.remove(subtask_table.getSelectedRow());
                refresh();
            }
        });
        btn_copy.addActionListener(e -> {
            if (subtask_table.getSelectedRow() != -1) {
                subTaskList.add(subTaskList.get(subtask_table.getSelectedRow()));
                refresh();
            }
        });
        btn_clearAll.addActionListener(e -> {

            AltoJsonWarning confirmation = new AltoJsonWarning("Warning: Clear all", "Are you sure you want to clear all items?");
            if (confirmation.OKPressed()) {
                subTaskList.clear();
            }
            refresh();
        });
        btn_save.addActionListener(e -> {
            dispose();
        });
        btn_Cancel.addActionListener(e -> {
            subTaskList.clear();
            dispose();
        });

        refresh();
    }

    private void openSubtaskMenu() {
         /*
                TODO
                    1. Launch sub task parameterizer dialogue [DONE]
                    2. Store the results to subTaskList [DONE]
                    3. refresh table [DONE]
                    [future]
                        Allow for selection of items in the table
                 */
        subtasks_parameterizer task_parameters_dialogue;
        boolean isEdit = false;
        int editIndex = -1;
        //previously added item task
        if (subtask_table.getSelectedRow() != -1) {
            task_parameters_dialogue =
                    new subtasks_parameterizer(subTaskList.get(subtask_table.getSelectedRow()));
            isEdit = true;
            editIndex = subtask_table.getSelectedRow();
            subTaskList.remove(subtask_table.getSelectedRow());
            refresh();
        } else {
            task_parameters_dialogue =
                    new subtasks_parameterizer(commandBox.getSelectedItem().toString()); //Ignore warning, will never be null
        }
        //compile tasks array to a json object
        if (isEdit) {
            if (!task_parameters_dialogue.shouldDiscard())
                subTaskList.add(editIndex, task_parameters_dialogue.getItems());
        } else {
            if (!task_parameters_dialogue.shouldDiscard())
                subTaskList.add(task_parameters_dialogue.getItems());
        } //TODO Allow multiple thins added
        refresh();
    }

    private void refresh() {
        //Refresh Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"command type", "command_param_preview"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        subtask_table.setModel(model);
        try {
            for (customSubTask t : subTaskList) {
                Object[] test = (Object[]) t.getParameters()[0];
                String elipse = t.getParameters().length > 1 ? " + " + (t.getParameters().length - 1) + " more" : "";
                model.addRow(new String[]{t.getType(), Arrays.toString(test) + elipse}); //could break if someone had a HUGE task
            }
            subtask_table.setModel(model);
        } catch (Exception ignored) {

        }
        //refresh buttons
        btn_add_sub_task.setText("+");
        btn_Remove.setEnabled(false);
        btn_copy.setEnabled(false);
        btn_clearAll.setEnabled(subTaskList.size() > 0);

        //TODO disable save button in bad instances

    }

    /**
     * compiles the components of a writeable task into a JSON
     *
     * @return the compiled components
     */
    public JSONObject write() {
        if (subTaskList.size() == 0) {
            throw new NullPointerException("Saved task contains no items");
        }
        if (tf_name.getText().isBlank() || tf_name.getText().isEmpty() || tf_name.getText().contains(" ")) { // no w h i t e s p a c e
            throw new NullPointerException("Saved task contains invalid name");
        }
        if (tf_desc.getText().isBlank() || tf_desc.getText().isEmpty()) {
            throw new NullPointerException("Saved task contains invalid description");
        }

//TODO Check for integers

        //name
        taskToWrite.add(new Key() {
            @Override
            public String getKey() {
                return "name";
            }

            @Override
            public Object getValue() {
                return tf_name.getText();
            }
        });
        //description
        taskToWrite.add(new Key() {
            @Override
            public String getKey() {
                return "description";
            }

            @Override
            public Object getValue() {
                return tf_desc.getText();
            }
        });
        //tasks
        JSONArray subTasks = new JSONArray();
        for (customSubTask t : subTaskList) {
            subTasks.put(t.build());
        }
        taskToWrite.add(new Key() {
            @Override
            public String getKey() {
                return "tasks";
            }

            @Override
            public Object getValue() {
                return subTasks;
            }
        });
        return taskToWrite.writeObject();

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
        main_panel = new JPanel();
        main_panel.setLayout(new GridLayoutManager(9, 6, new Insets(4, 4, 4,
                4), -1, -1));
        subtask_table = new JTable();
        main_panel.add(subtask_table, new GridConstraints(2, 5, 7, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150
                , 50), null, 0, false));
        l_command = new JLabel();
        l_command.setText("Command");
        main_panel.add(l_command, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        commandBox = new JComboBox();
        main_panel.add(commandBox, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, new Dimension(5, 5),
                new Dimension(80, 20), new Dimension(100, 20), 1, false));
        l_param = new JLabel();
        l_param.setText("parameters");
        main_panel.add(l_param, new GridConstraints(2, 1, 1, 3,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_save = new JButton();
        btn_save.setText("Save");
        main_panel.add(btn_save, new GridConstraints(7, 0, 1, 5,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        main_panel.add(spacer1, new GridConstraints(6, 0, 1, 4,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        btn_Cancel = new JButton();
        btn_Cancel.setText("Cancel");
        main_panel.add(btn_Cancel, new GridConstraints(8, 0, 1, 5,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Name");
        main_panel.add(label1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        tf_name = new JTextField();
        main_panel.add(tf_name, new GridConstraints(0, 1, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150,
                -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Description");
        main_panel.add(label2, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tf_desc = new JTextField();
        main_panel.add(tf_desc, new GridConstraints(1, 1, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150,
                -1), null, 0, false));
        btn_add_sub_task = new JButton();
        btn_add_sub_task.setText("add parameters");
        main_panel.add(btn_add_sub_task, new GridConstraints(3, 2, 1, 3,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_clearAll = new JButton();
        btn_clearAll.setText("Clear All");
        main_panel.add(btn_clearAll, new GridConstraints(5, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 25), null, 0, false));
        btn_Remove = new JButton();
        btn_Remove.setText("Remove");
        main_panel.add(btn_Remove, new GridConstraints(4, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 25), null, 0, false));
        btn_copy = new JButton();
        btn_copy.setText("Copy");
        main_panel.add(btn_copy, new GridConstraints(4, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 25), null, 0, false));
        l_task_preview = new JLabel();
        l_task_preview.setText("Tasks");
        main_panel.add(l_task_preview, new GridConstraints(1, 5, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main_panel;
    }
}
