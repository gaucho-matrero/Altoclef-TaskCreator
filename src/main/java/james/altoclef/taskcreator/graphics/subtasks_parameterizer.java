package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.customSubTask;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class subtasks_parameterizer extends JDialog {
    private JPanel main_panel;
    private JComboBox<String> item_selector;
    private JTextField broad_parameter;
    private JTable table1;
    private JButton btn_plus;
    private JButton btn_done;
    private JLabel l_type;

    private final List<Object> params;
    private final String command;
    public subtasks_parameterizer(String command) {
        setContentPane(main_panel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setModal(true);
        this.command = command;
        params = new ArrayList<Object>();
        setVisible(true); // must always be last
    }

    private void initComponents() {
        //TODO create action listeners. See New Task Frame
    }


    public customSubTask getItems() {
       return new customSubTask(l_type.getText(),params.toArray());
    }
}
