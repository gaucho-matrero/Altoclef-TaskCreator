package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AltoJsonWarning extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel l_warning_text;
    private boolean ok_pressed = false;

    public AltoJsonWarning() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    public AltoJsonWarning(String message){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./img/warning.png");
        setIconImage(icon);
        setTitle("Warning: Cannot load JSON file");
        setL_warning_text("<html><center><p style=\"width:300px\">" + message + "</p></center></html>");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        ok_pressed = true;
        dispose();
    }

    public boolean OKPressed() {
        return this.ok_pressed;
    }

    public void setL_warning_text(String s) {
        l_warning_text.setText(s);
    }



}
