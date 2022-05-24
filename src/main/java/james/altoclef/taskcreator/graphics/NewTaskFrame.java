package james.altoclef.taskcreator.graphics;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class NewTaskFrame extends JFrame {
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
    private JSONObject customTask;

    public NewTaskFrame(JSONObject customTask) {
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
        btn_add_sub_task.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subtasks_parameterizer stp;
                switch ((Objects.requireNonNull(commandBox.getSelectedItem()).toString())) {
                    case "goto" -> stp = new subtasks_parameterizer("coords");
                    case "get" -> stp = new subtasks_parameterizer("item");
                    case "punk" -> stp = new subtasks_parameterizer("player");
                    default -> doNothing();
                }

            }

            private void doNothing() {
                //does nothing. Should never happen
            }
        });

    }

}
