package Kimmy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SmartNotes extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    // Interface Components
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu optionsMenu;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;
    private JMenuItem fontItem;
    private JMenuItem bgColorItem;

    private Point initialPoint; // For gesture detection

    // Constructor
    public SmartNotes() {
        // Frame setup
        setTitle("SmartNotes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Text area creation
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Mouse listeners for gesture recognition
        textArea.addMouseListener(this);
        textArea.addMouseMotionListener(this);

        // Menu bar creation
        menuBar = new JMenuBar();

        // File menu creation
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Options menu creation
        optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        // Menu items creation
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");
        fontItem = new JMenuItem("Change Font");
        bgColorItem = new JMenuItem("Change Background Color");

        // Adding menu items to File menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Adding menu items to Options menu
        optionsMenu.add(fontItem);
        optionsMenu.add(bgColorItem);

        // Attached Action listeners for menu items
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);
        fontItem.addActionListener(this);
        bgColorItem.addActionListener(this);

        // Place the menu bar for the frame
        setJMenuBar(menuBar);
    }

    // Insert Action listener for menu items
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openItem) {
            openFile();
        } else if (e.getSource() == saveItem) {
            saveFile();
        } else if (e.getSource() == exitItem) {
            System.exit(0);
        } else if (e.getSource() == fontItem) {
            changeFont();
        } else if (e.getSource() == bgColorItem) {
            changeBackgroundColor();
        }
    }

    // Procedure to open a file
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
            }
        }
    }

    // Procedure to save a file
    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    // Method to change the font
    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Select Font", "Font Selection",
                JOptionPane.PLAIN_MESSAGE, null, fonts, textArea.getFont().getFamily());
        if (selectedFont != null) {
            textArea.setFont(new Font(selectedFont, Font.PLAIN, textArea.getFont().getSize()));
        }
    }

    // The Process to change the background color
    private void changeBackgroundColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Background Color", textArea.getBackground());
        if (newColor != null) {
            textArea.setBackground(newColor);
        }
    }

    // Attached MouseListener and MouseMotionListener methods tools for gesture detection
    @Override
    public void mousePressed(MouseEvent e) {
        initialPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point finalPoint = e.getPoint();
        if (initialPoint != null && finalPoint != null) {
            double distance = finalPoint.getX() - initialPoint.getX();
            if (distance > textArea.getWidth() / 2) {
                textArea.setText(""); // Remove the text area on significant right swipe
                JOptionPane.showMessageDialog(this, "Text area cleared with a gesture");
            }
        }
        initialPoint = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Additional gesture tracking can be added here
    }

    // Unused MouseMotionListener methods
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    // Main Procedure
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmartNotes notepad = new SmartNotes();
            notepad.setVisible(true);
        });
    }
}