package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.customSubTask;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class subtasks_parameterizer extends JDialog {
    private JPanel main_panel;
    private JComboBox<String> item_selector;
    private JTextField broad_parameter;
    private JTable table1;
    private JButton btn_plus;
    private JButton btn_done;
    private JLabel l_type;
    private final String command;
    private JSONArray subTasks;

    public subtasks_parameterizer(String command) {
        setContentPane(main_panel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setModal(true);
        this.command = command;
        initComponents();

    }

    private void initComponents() {
        l_type.setText(command);
        switch (command) {
            case "get" -> {
                item_selector.setVisible(true);
                item_selector.addItem("diamond");
            }
            //other items here
            case "goto" -> {
                item_selector.setVisible(true);
                item_selector.addItem("overworld");
                item_selector.addItem("nether");
            }
            default -> item_selector.setVisible(false);


        }
        {
            btn_done.addActionListener(new ActionListener() {
                /**
                 * Invoked when an action occurs.
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            btn_plus.addActionListener(new ActionListener() {
                /**
                 * Invoked when an action occurs.
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        customSubTask cST = null; //TODO add the other cases
                        switch (command) {
                            case "get":
                                cST = new customSubTask("get", new Object[]{item_selector.getItemAt(0), Integer.parseInt(broad_parameter.getText())});
                        }
                        subTasks.put(cST);
                    } catch (Exception ex) {
                        AltoJsonWarning aw = new AltoJsonWarning("Unable to add command");
                    }
                }
            });

        }//action listeners
        setVisible(true);

    }

    private void refreshTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Command", "param"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(model);

        for (int i = 0; i < subTasks.length(); i++) {
            //TODO add the rows of the subtask list.
        }
    }
}
