package james.altoclef.taskcreator.graphics;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import james.altoclef.taskcreator.utils.JSONManager;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StringImporter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField shareString;
    private JSONObject decompresssed = null;

    public StringImporter() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        JSONManager temp = new JSONManager();
        try {
            decompresssed = temp.decompressToJson(shareString.getText());
        } catch (Exception e) {
            decompresssed = new JSONObject();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        StringImporter dialog = new StringImporter();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public JSONObject getDecompresssed() {
        return decompresssed;
    }

}
