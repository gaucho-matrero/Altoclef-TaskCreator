package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.interfaces.Key;
import james.altoclef.taskcreator.writeableTask;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
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
    private writeableTask customTask;
    private List<writeableTask> subTasks;
    public NewTaskFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setContentPane(main_panel);
        subTasks = new ArrayList<>();
        customTask = null;
        initComponents();
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        getRootPane().setDefaultButton(btn_save);

    }
    public NewTaskFrame(writeableTask customTask) { //for edit
        setContentPane(main_panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.customTask = customTask;
        initComponents();
    }

    private void initComponents() {
        // Command combo box
        commandBox.addItem("goto");
        commandBox.addItem("get");
        commandBox.addItem("punk");
        commandBox.addItem("deposit");
        commandBox.addItem("gamer");
        commandBox.setSelectedItem("get");
        //parameter dialogue
        if(Objects.requireNonNull(commandBox.getSelectedItem()).toString().equals("")){
            btn_add_sub_task.setEnabled(false);
        }
        {
            btn_add_sub_task.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    subtasks_parameterizer stp;
                    switch ((Objects.requireNonNull(commandBox.getSelectedItem()).toString())) {
                        case "goto" -> stp = new subtasks_parameterizer("goto");
                        case "get" -> stp = new subtasks_parameterizer("get");
                        case "punk" -> stp = new subtasks_parameterizer("punk");
                        default -> doNothing();
                    }

                }

                private void doNothing() {
                    //does nothing. Should never happen
                }
            });
            btn_save.addActionListener(new ActionListener() {
                /**
                 * Invoked when an action occurs.
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(tf_name.getText().isBlank()|| tf_desc.getText().isBlank()){
                        AltoJsonWarning warning = new AltoJsonWarning("Name and description cannot be blank");
                    }else{
                        JSONArray toStore = new JSONArray();
                        customTask = new writeableTask();
                        customTask.add(new Key() {
                            @Override
                            public String getKey() {
                                return "name";
                            }

                            @Override
                            public Object getValue() {
                                return tf_name.getText();
                            }
                        });
                        customTask.add(new Key() {
                            @Override
                            public String getKey() {
                                return "description";
                            }

                            @Override
                            public Object getValue() {
                                return tf_desc.getText();
                            }
                        });
                        for (writeableTask t : subTasks) {
                            toStore.put(t.writeObject());
                        }
                        customTask = new writeableTask();
                        customTask.add(new Key() {
                            @Override
                            public String getKey() {
                                return "tasks";
                            }

                            @Override
                            public Object getValue() {
                                return toStore;
                            }
                        });
                        dispose();
                    }
                    //write(); may or may not be needed
                }
            });
        }//buttons

    }

    public JSONObject write() {
        //TODO temp, use writeableTask.write
        return customTask.writeObject();
    }

    public static void main(String[] args) {
        NewTaskFrame nTF = new NewTaskFrame();
        nTF.pack();
        nTF.setVisible(true);
        System.exit(0);
    }
}
