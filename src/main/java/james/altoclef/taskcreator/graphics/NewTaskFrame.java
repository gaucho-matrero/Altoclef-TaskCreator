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
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private JSONObject input;
    private final List<customSubTask> subTaskList;
    private boolean dispose;
    private List<customSubTask> original;

    public NewTaskFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setContentPane(main_panel);
        input = null;
        setSize(950, 520);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btn_save);
        subTaskList = new ArrayList<customSubTask>();
        dispose = true;
        original = new ArrayList<>(subTaskList);

        //nothing below this line
        initComponents();
        setVisible(true);//must always be last

    }

    public NewTaskFrame(JSONObject arg) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setContentPane(main_panel);
        setSize(950, 520);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btn_save);
        subTaskList = new ArrayList<customSubTask>();
        input = arg;
        dispose = false;
        original = new ArrayList<>(subTaskList);

        //nothing below this line
        initComponents();
        setVisible(true);//must always be last
    }

    private void initComponents() {
        //TODO combo box
        commandBox.addItem("get");
        commandBox.addItem("goto");
        commandBox.addItem("punk");
        commandBox.addItem("status");


        addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of
             * being closed. The close operation can be
             * overridden at this point.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
                super.windowClosing(e);
            }
        });

        subtask_table.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                commandBox.setSelectedItem("get");
                if (e.getClickCount() == 2 && subtask_table.getSelectedRow() != -1) {
                    if (!subTaskList.get(subtask_table.getSelectedRow()).getType().equals("status"))
                        openSubtaskMenu();
                    else {
                        AltoJsonWarning warning = new AltoJsonWarning("Invalid selection", "You can't edit an action that doesn't require parameters");
                    }
                } else {
                    if (subtask_table.getSelectedRow() != -1) {
                        btn_add_sub_task.setText("Edit");
                        btn_copy.setEnabled(true);
                        btn_add_sub_task.setEnabled(!subTaskList.get(subtask_table.getSelectedRow()).getType().equals("status"));
                        btn_Remove.setEnabled(true);
                    }
                }
            }


        });

        subtask_table.addKeyListener(new KeyAdapter() {
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
                    subtask_table.clearSelection();
                    btn_add_sub_task.setText("add parameters");
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
                btn_save.setEnabled(true);
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
            dispose = false;
            dispose();
        });
        btn_Cancel.addActionListener(e -> {
            cancel();
        });
        if (input != null) {
            loadInput();
        }
        refresh();
    }

    private void cancel() {
        if (input != null) {
            dispose = false;
        }
        subTaskList.clear();
        subTaskList.addAll(original);
        dispose();
    }

    private void loadInput() {
        tf_name.setText((String) input.get("name"));
        tf_desc.setText(input.getString("description"));
        JSONArray arToIterate = input.getJSONArray("tasks");
        for (int i = 0; i < arToIterate.length(); i++) {
            String type = arToIterate.getJSONObject(i).getString("command");
            List<Object> tos;
            try {
                tos = arToIterate.getJSONObject(i).getJSONArray("parameters").toList(); //TODO This fails if you edit it twice. Fix it
                Object[][] items = new Object[tos.size()][];
                //Object[number of subtasks][parameter size of subtask (get is 2, goto is 3, punk is 1, etc]
                for (int j = 0; j < tos.size(); j++) {
                    items[j] = (tos.get(j).toString()).replaceAll("\\[", "").replaceAll("]", "").split(",");
                }
                subTaskList.add(new customSubTask(type, items));
            } catch (Exception e) { //excepts because json arrays only exist on read in. They become objects arrays after we save
                tos = new ArrayList<>();
                Object[] parameters = (Object[]) arToIterate.getJSONObject(i).get("parameters");
                for (Object parameter : parameters) {
                    tos.add((Arrays.asList((Object[]) parameter)));
                }
                Object[][] items = new Object[tos.size()][];
                //Object[number of subtasks][parameter size of subtask (get is 2, goto is 3, punk is 1, etc]
                for (int k = 0; k < tos.size(); k++) {
                    items[k] = (tos.get(k).toString()).replaceAll("\\[", "").replaceAll("]", "").split(",");
                }
                subTaskList.add(new customSubTask(type, items));
                tos.clear();
            }
        }
        original = new ArrayList<>(subTaskList);

    }

    private void openSubtaskMenu() {
        if (commandBox.getSelectedItem().equals("status")) {
            subTaskList.add(new customSubTask("status", new Object[]{new Object[]{}}));
        } else {
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
            } //TODO Allow multiple things added
        }
        commandBox.setSelectedItem("get");
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
        btn_add_sub_task.setText("add");
        btn_Remove.setEnabled(false);
        btn_copy.setEnabled(false);
        btn_clearAll.setEnabled(subTaskList.size() > 0);
        btn_save.setEnabled(!subTaskList.equals(original) && subTaskList.size() != 0);
        //TODO Update on title & description change
    }

    /**
     * compiles the components of a writeable task into a JSON
     *
     * @return the compiled components
     */
    public JSONObject write() {
        if (!dispose) {
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

        } else {
            return null; //do nothing
        }
    }

}
