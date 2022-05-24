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

    public static void main(String[] args) {
        AltoJsonWarning dialog = new AltoJsonWarning();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


}
