package james.altoclef.taskcreator.graphics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class subtasks_parameterizer extends JFrame {
    private JPanel main_panel;
    private JComboBox<String> item_selector;
    private JTextField broad_parameter;
    private JTable table1;
    private JButton btn_plus;
    private JButton btn_done;
    private JLabel l_type;
    private final String command;
    public subtasks_parameterizer(String command) {
        setContentPane(main_panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

                }
            });
        }//action listeners

    }

}
