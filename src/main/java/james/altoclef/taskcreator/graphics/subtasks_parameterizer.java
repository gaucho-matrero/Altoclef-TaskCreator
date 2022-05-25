package james.altoclef.taskcreator.graphics;

import james.altoclef.taskcreator.MinecraftUtil;
import james.altoclef.taskcreator.customSubTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class subtasks_parameterizer extends JDialog {
    private JPanel main_panel;
    private JComboBox<String> item_selector;
    private JTable action_table;
    private JButton btn_add;
    private JButton btn_done;
    private JLabel l_type;
    private JComboBox combo_x;
    private JComboBox combo_y;
    private JComboBox combo_z;
    private JComboBox<String> combo_itemCount_or_dimension;
    private JTextField tf_target;

    private final List<Object> params;
    private final String command;
    public subtasks_parameterizer(String command) {
        setContentPane(main_panel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setModal(true);
        this.command = command;
        params = new ArrayList<Object>();
        initComponents();
        setVisible(true); // must always be last

    }

    private void initComponents() {
        //TODO create action listeners. See New Task Frame

        {
            switch (command) {
                case "get" -> loadUI_items();
                case "goto" -> loadUI_coords();
                default -> loadUI();
            }
        } // ui settings

        {
            btn_add.addActionListener(new ActionListener() {
                /**
                 * Invoked when an action occurs.
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    /*
                    TODO
                        Add other tasks here
                     */
                    if(command.equals("get"))
                        params.add(new Object[]{item_selector.getSelectedItem(),combo_itemCount_or_dimension.getSelectedItem()});
                    else if(command.equals("goto"))
                        params.add(new Object[]{combo_x.getSelectedItem(),combo_y.getSelectedItem(),combo_z,combo_itemCount_or_dimension.getSelectedItem()});
                    else
                        params.add(new Object[]{tf_target.getText()});

                    refreshTable();
                }
            });
        } // action listeners
    }

    private void refreshTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"parameters"},0);
        action_table.setModel(model);
        try{
            for(customSubTask) //TODO for each sub task, add it to the table FINISH
        }catch (Exception ignored){
            AltoJsonWarning aw = new AltoJsonWarning("Table error","error loading table");
            aw.setVisible(true);
        }
    }

    private void loadUI() {
        l_type.setText(command);
        item_selector.setVisible(false);
        combo_x.setVisible(false);
        combo_y.setVisible(false);
        combo_z.setVisible(false);
        tf_target.setVisible(true);
    }

    private void loadUI_coords() {
        l_type.setText("goto");
        item_selector.setVisible(false);
        tf_target.setVisible(false);
        combo_x.setVisible(true);
        combo_y.setVisible(true);
        combo_z.setVisible(true);
        combo_itemCount_or_dimension.addItem("overworld");
        combo_itemCount_or_dimension.addItem("nether");
        combo_itemCount_or_dimension.addItem("end");
        combo_itemCount_or_dimension.addItem(""); //whatever current dimension is
    }

    private void loadUI_items() {
        l_type.setText("get");
        combo_x.setVisible(false);
        combo_y.setVisible(false);
        combo_z.setVisible(false);
        tf_target.setVisible(false);
        loadItemComboBoxes();
    }

    private void loadItemComboBoxes() {
        for(String s: MinecraftUtil.getItems()){
            item_selector.addItem(s);
        }
        for(int i=1; i<65; i++){
            combo_itemCount_or_dimension.addItem(i+"");
        }
    }


    public customSubTask getItems() {
       return new customSubTask(l_type.getText(),params.toArray());
    }
}
