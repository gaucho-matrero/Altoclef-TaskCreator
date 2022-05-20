package james.altoclef.taskcreator.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AltoFrame extends JFrame implements ActionListener {
    JPanel panel = new JPanel(new GridLayout(3,3));
    public AltoFrame(){
        super("Altoclef-TaskCreator");
    }

    public void build(){
        this.setDefaultCloseOperation(AltoFrame.EXIT_ON_CLOSE);
        this.setSize(750,750);
        add(panel);
        addMenu();
        addButtonTest();
        this.setVisible(true);
    }

    private void addMenu(){
        JMenuBar topMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenu_newFile = new JMenuItem("New");
        JMenu fileMenu_subMenuOpen = new JMenu("Open");
        JMenuItem fileMenu_subMenuOpen_fromFileSystem = new JMenuItem("From File System");
        JMenuItem fileMenu_subMenuOpen_fromString = new JMenuItem("From String");
        fileMenu.add(fileMenu_newFile);
        fileMenu_subMenuOpen.add(fileMenu_subMenuOpen_fromFileSystem);
        fileMenu_subMenuOpen.add(fileMenu_subMenuOpen_fromString);
        fileMenu.add(fileMenu_subMenuOpen);

        JMenu options = new JMenu("Tools");
        JMenuItem options_viewUsageGuide = new JMenuItem("Usage Guide");
        options.add(options_viewUsageGuide);
        topMenu.add(fileMenu);
        topMenu.add(options);
        this.add(topMenu, BorderLayout.NORTH);
    }
    private void addButtonTest(){
        JButton compileButton = new JButton("Compile");
        compileButton.setActionCommand("compile");
        compileButton.addActionListener(this);
        compileButton.setSize(150,150);
        panel.add(compileButton);
    }
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "compile" -> this.setBackground(Color.gray);
        }
    }
}
