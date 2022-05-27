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
    
    private void initComponents(){

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
                if(e.getClickCount()==2 && subtask_table.getSelectedRow()!=-1){
                    openSubtaskMenu();
                }else {
                    if (subtask_table.getSelectedRow() != -1) {
                        btn_add_sub_task.setText("Edit");
                        btn_copy.setEnabled(true);
                        btn_Remove.setEnabled(true);
                    }
                }
            }


        });
        btn_add_sub_task.addActionListener(e->{
            openSubtaskMenu();
        });
        btn_Remove.addActionListener(e->{
            if(subtask_table.getSelectedRow()!=-1){
                subTaskList.remove(subtask_table.getSelectedRow());
                refresh();
            }
        });
        btn_copy.addActionListener(e -> {
            if(subtask_table.getSelectedRow()!=-1){
                subTaskList.add(subTaskList.get(subtask_table.getSelectedRow()));
                refresh();
            }
        });
        btn_clearAll.addActionListener(e -> {

            AltoJsonWarning confirmation = new AltoJsonWarning("Warning: Clear all", "Are you sure you want to clear all items?");
            if(confirmation.OKPressed()){
                subTaskList.clear();
            }
            refresh();
        });
        btn_save.addActionListener(e->{
            dispose();
        });
        btn_Cancel.addActionListener(e->{
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
        int editIndex=-1;
        //previously added item task
        if(subtask_table.getSelectedRow()!=-1){
            task_parameters_dialogue =
                    new subtasks_parameterizer(subTaskList.get(subtask_table.getSelectedRow()));
            isEdit=true;
            editIndex = subtask_table.getSelectedRow();
            subTaskList.remove(subtask_table.getSelectedRow());
            refresh();
        }else {
            task_parameters_dialogue =
                    new subtasks_parameterizer(commandBox.getSelectedItem().toString()); //Ignore warning, will never be null
        }
        //compile tasks array to a json object
        if(isEdit){
            if(!task_parameters_dialogue.shouldDiscard())
                subTaskList.add(editIndex,task_parameters_dialogue.getItems());
        }else {
            if (!task_parameters_dialogue.shouldDiscard())
                subTaskList.add(task_parameters_dialogue.getItems());
        } //TODO Allow multiple thins added
        refresh();
    }

    private void refresh() {
        //Refresh Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"command type","command_param_preview"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        subtask_table.setModel(model);
        try{
            for(customSubTask t: subTaskList){
                Object[] test = (Object[]) t.getParameters()[0];
                String elipse = t.getParameters().length>1 ? " + " + (t.getParameters().length - 1) + " more" : "";
                model.addRow(new String[]{t.getType(), Arrays.toString(test) + elipse}); //could break if someone had a HUGE task
            }
            subtask_table.setModel(model);
        }catch (Exception ignored){

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
     * @return the compiled components
     */
    public JSONObject write() {
        if(subTaskList.size()==0){
            throw new NullPointerException("Saved task contains no items");
        }
        if(tf_name.getText().isBlank() || tf_name.getText().isEmpty() || tf_name.getText().contains(" ")){ // no w h i t e s p a c e
            throw new NullPointerException("Saved task contains invalid name");
        }
        if(tf_desc.getText().isBlank() || tf_desc.getText().isEmpty()){
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
