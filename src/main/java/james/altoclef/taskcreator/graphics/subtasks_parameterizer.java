package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import james.altoclef.taskcreator.MinecraftUtil;
import james.altoclef.taskcreator.customSubTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class subtasks_parameterizer extends JDialog {
    private JPanel main_panel;
    private JComboBox<String> item_selector;
    private JTable action_table;
    private JButton btn_add;
    private JButton btn_done;
    private JLabel l_type;
    private JComboBox<String> combo_itemCount_or_dimension;
    private JTextField tf_target;
    private JTextField tf_x;
    private JTextField tf_y;
    private JTextField tf_z;
    private int editing = -1;
    private final List<Object> params;
    private final String command;
    private boolean discard = true;

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

    public subtasks_parameterizer(customSubTask task) {
        setContentPane(main_panel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setModal(true);
        this.command = task.getType();
        params = new ArrayList<Object>();
        Collections.addAll(params, task.getParameters());
        if (!task.getType().equals("get")) {
            toggleContinuedAdd(false);
        }
        discard = false;
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

            action_table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (action_table.getSelectedRow() != -1) {
                            editing = action_table.getSelectedRow();
                            toggleContinuedAdd(true);
                            btn_add.setText("Save");
                        }
                    }
                }
            });

            // ui settings
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
                    if (command.equals("get")) {
                        if (editing != -1) {
                            params.remove(editing);
                            params.add(editing, new Object[]{item_selector.getSelectedItem(), combo_itemCount_or_dimension.getSelectedItem()});
                        } else {
                            params.add(new Object[]{item_selector.getSelectedItem(), combo_itemCount_or_dimension.getSelectedItem()});
                        }
                        toggleContinuedAdd(true);
                    } else if (command.equals("goto")) {
                        if (editing != -1) {
                            params.remove(editing);
                            params.add(editing, new Object[]{tf_x.getText(), tf_y.getText(), tf_z.getText(), combo_itemCount_or_dimension.getSelectedItem()});
                        } else {
                            params.add(new Object[]{tf_x.getText(), tf_y.getText(), tf_z.getText(), combo_itemCount_or_dimension.getSelectedItem()});
                        }
                        toggleContinuedAdd(false);
                    } else {
                        if (editing != -1) {
                            params.remove(editing);
                            params.add(editing, new Object[]{tf_target.getText()});

                        } else {
                            params.add(new Object[]{tf_target.getText()});
                        }
                        toggleContinuedAdd(false);
                    }

                    editing = -1;
                    btn_add.setText("add");
                    refreshTable();
                }
            });
            btn_done.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onDone();
                }
            });
        } // action listeners
        refreshTable();
    }

    private void toggleContinuedAdd(boolean b) {
        if (!b) {
            item_selector.setEnabled(false);
            combo_itemCount_or_dimension.setEnabled(false);
            tf_x.setEnabled(false);
            tf_y.setEnabled(false);
            tf_z.setEnabled(false);
            tf_target.setEnabled(false);
            btn_add.setEnabled(false);
        } else {
            item_selector.setEnabled(true);
            combo_itemCount_or_dimension.setEnabled(true);
            tf_x.setEnabled(true);
            tf_y.setEnabled(true);
            tf_z.setEnabled(true);
            tf_target.setEnabled(true);
            btn_add.setEnabled(true);
        }
    }

    private void onDone() {
        discard = false;
        dispose();
    }

    private void refreshTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"parameters"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        action_table.setModel(model);
        try {
            for (Object param : params) {
                Object[] param_inner = (Object[]) param; //will always be an object
                model.addRow(new String[]{Arrays.toString(param_inner)});
            }
        } catch (Exception ignored) {
            AltoJsonWarning aw = new AltoJsonWarning("Table error", "error loading table");
            aw.setVisible(true);
        }

    }

    private void loadUI() {
        l_type.setText(command);
        item_selector.setVisible(false);
        combo_itemCount_or_dimension.setVisible(false);
        tf_x.setVisible(false);
        tf_y.setVisible(false);
        tf_z.setVisible(false);
        tf_target.setVisible(true);
    }

    private void loadUI_coords() {
        l_type.setText("goto");
        item_selector.setVisible(false);
        tf_target.setVisible(false);
        tf_x.setVisible(true);
        tf_y.setVisible(true);
        tf_z.setVisible(true);
        combo_itemCount_or_dimension.setVisible(true);
        combo_itemCount_or_dimension.addItem("overworld");
        combo_itemCount_or_dimension.addItem("nether");
        combo_itemCount_or_dimension.addItem("end");
        combo_itemCount_or_dimension.addItem(""); //whatever current dimension is

    }

    private void loadUI_items() {
        l_type.setText("get");
        tf_x.setVisible(false);
        tf_y.setVisible(false);
        tf_z.setVisible(false);
        tf_target.setVisible(false);
        loadItemComboBoxes();
    }

    private void loadItemComboBoxes() {
        for (String s : MinecraftUtil.getItems()) {
            item_selector.addItem(s);
        }
        for (int i = 1; i < 65; i++) {
            combo_itemCount_or_dimension.addItem(i + "");
        }
    }


    public customSubTask getItems() {
        return new customSubTask(l_type.getText(), params.toArray());
    }

    public boolean shouldDiscard() {
        return discard;
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
        main_panel.setLayout(new GridLayoutManager(6, 7, new Insets(4, 4, 4,
                4), -1, -1));
        item_selector = new JComboBox();
        main_panel.add(item_selector, new GridConstraints(1, 0, 1, 6,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        l_type = new JLabel();
        l_type.setText("Type");
        main_panel.add(l_type, new GridConstraints(0, 0, 1, 6,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        action_table = new JTable();
        main_panel.add(action_table, new GridConstraints(0, 6, 6, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150
                , 50), null, 0, false));
        btn_add = new JButton();
        btn_add.setText("add");
        main_panel.add(btn_add, new GridConstraints(5, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_done = new JButton();
        btn_done.setText("Done");
        main_panel.add(btn_done, new GridConstraints(5, 5, 1, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        combo_itemCount_or_dimension = new JComboBox();
        main_panel.add(combo_itemCount_or_dimension, new GridConstraints(2, 0
                , 1, 6, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1)
                , null, 0, false));
        tf_target = new JTextField();
        main_panel.add(tf_target, new GridConstraints(4, 0, 1, 6,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150,
                -1), null, 0, false));
        tf_x = new JTextField();
        main_panel.add(tf_x, new GridConstraints(3, 0, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1)
                , null, 1, false));
        tf_y = new JTextField();
        main_panel.add(tf_y, new GridConstraints(3, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1)
                , null, 0, false));
        tf_z = new JTextField();
        main_panel.add(tf_z, new GridConstraints(3, 3, 1, 3,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1)
                , null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main_panel;
    }
}
