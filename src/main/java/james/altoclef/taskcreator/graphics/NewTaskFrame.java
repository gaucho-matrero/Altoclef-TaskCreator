package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.customSubTask;
import james.altoclef.taskcreator.interfaces.Key;
import james.altoclef.taskcreator.writeableTask;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewTaskFrame extends JDialog {
    private JTable subtask_table;
    private JComboBox<String> commandBox;
    private JLabel l_command;
    private JButton btn_add_sub_task;
    private JLabel l_param;
    private JButton btn_add;
    private JButton btn_Remove;
    private JButton btn_save;
    private JButton btn_Cancel;
    private JPanel main_panel;
    private JTextField tf_name;
    private JTextField tf_desc;

    private final writeableTask taskToWrite;
    private List<customSubTask> subTaskList;

    public NewTaskFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setContentPane(main_panel);
        initComponents();
        setSize(500, 300);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btn_save);
        taskToWrite = null;
        subTaskList = new ArrayList<customSubTask>();
        setVisible(true); //must always be last
    }
    
    private void initComponents(){
        btn_add_sub_task.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                TODO
                    1. Launch sub task parameterizer dialogue [DONE]
                    2. Store the results to subTaskList [DONE]
                    3. refresh table [DONE]
                    [future]
                        Allow for selection of items in the table
                 */
                subtasks_parameterizer task_parameters_dialogue = new subtasks_parameterizer(commandBox.getActionCommand());
                //compile tasks array to a json object
                subTaskList.add(task_parameters_dialogue.getItems());
                refreshTable();
            }
        });
        setVisible(true);//must always be last
    }

    private void refreshTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"command type","command_param_preview"},0);
        subtask_table.setModel(model);
        try{
            for(customSubTask t: subTaskList){
                model.addRow(new Object[]{t.getType(),t.getParameters()}); //could break if someone had a HUGE task
            }
            subtask_table.setModel(model);
        }catch (Exception ignored){

        }
    }

    /**
     * compiles the components of a writeable task into a JSON
     * @return the compiled components
     */
    public JSONObject write() {
        //name
        taskToWrite.add(new Key() {
            @Override
            public String getKey() {
                return "name";
            }

            @Override
            public Object getValue() {
                return tf_name;
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
                return tf_desc;
            }
        });
        //tasks
        JSONArray subTasks = new JSONArray();
        for(customSubTask t: subTaskList){
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
}
