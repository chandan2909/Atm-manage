package Atm.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Pin extends JFrame implements ActionListener, ComponentListener {
    JButton b1,b2;
    JPasswordField p1,p2;
    String pin;
    String cardno;
    private JLabel backgroundLabel;
    private ImageIcon originalIcon;
    private Image originalImage;
    
    Pin(String pin){
        this.pin = pin;  // This is already a hashed PIN
        
        // Set window properties
        setLayout(null);
        setUndecorated(false);
        setResizable(true);
        
        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Silently handle icon loading failure
        }
        
        // Create background panel with ATM color
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(92, 96, 95));  // #5c605f
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, 
                                      Toolkit.getDefaultToolkit().getScreenSize().height);
        add(backgroundPanel);
        
        // Load original image
        originalIcon = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        originalImage = originalIcon.getImage();
        
        // Create background label
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(null);
        backgroundPanel.add(backgroundLabel);

        // Get card number from database
        getCardNumber();

        // Add components
        setupComponents();
        
        // Set window behavior
        addComponentListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Initial scaling
        scaleImage();
        setVisible(true);
    }

    private void getCardNumber() {
        try (Connection conn = Connn.getConnection()) {
            String query = "SELECT cardno FROM login WHERE pin = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, pin);  // Use PIN directly as it's already hashed
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.cardno = rs.getString("cardno");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error retrieving account information");
        }
    }

    private void setupComponents() {
        JLabel label1 = new JLabel("CHANGE YOUR PIN");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(430,180,400,35);
        backgroundLabel.add(label1);

        JLabel label2 = new JLabel("New PIN: ");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(430,220,150,35);
        backgroundLabel.add(label2);

        p1 = new JPasswordField();
        p1.setBackground(new Color(65,125,128));
        p1.setForeground(Color.WHITE);
        p1.setBounds(600,220,180,25);
        p1.setFont(new Font("Raleway", Font.BOLD,22));
        backgroundLabel.add(p1);

        JLabel label3 = new JLabel("Re-Enter New PIN: ");
        label3.setForeground(Color.WHITE);
        label3.setFont(new Font("System", Font.BOLD, 16));
        label3.setBounds(430,250,400,35);
        backgroundLabel.add(label3);

        p2 = new JPasswordField();
        p2.setBackground(new Color(65,125,128));
        p2.setForeground(Color.WHITE);
        p2.setBounds(600,255,180,25);
        p2.setFont(new Font("Raleway", Font.BOLD,22));
        backgroundLabel.add(p2);

        b1 = new JButton("CHANGE");
        b1.setBounds(700,362,150,35);
        b1.setBackground(new Color(65,125,128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        backgroundLabel.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(700,406,150,35);
        b2.setBackground(new Color(65,125,128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        backgroundLabel.add(b2);
    }

    private void scaleImage() {
        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        
        // Scale image to maintain aspect ratio while filling the screen
        double imageAspect = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
        double screenAspect = (double) screenSize.width / screenSize.height;
        
        int scaledWidth, scaledHeight;
        if (screenAspect > imageAspect) {
            scaledWidth = screenSize.width;
            scaledHeight = (int) (screenSize.width / imageAspect);
        } else {
            scaledHeight = screenSize.height;
            scaledWidth = (int) (screenSize.height * imageAspect);
        }
        
        // Center the image
        int x = (screenSize.width - scaledWidth) / 2;
        int y = (screenSize.height - scaledHeight) / 2;
        backgroundLabel.setBounds(x, y, scaledWidth, scaledHeight);
        
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_AREA_AVERAGING);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String pin1 = new String(p1.getPassword());
            String pin2 = new String(p2.getPassword());

            if (e.getSource() == b1) {
                if (pin1.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter New PIN");
                    return;
                }
                if (pin2.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Re-Enter New PIN");
                    return;
                }
                
                if (!pin1.equals(pin2)) {
                    JOptionPane.showMessageDialog(null, "Entered PINs do not match");
                    return;
                }

                if (pin1.length() != 4) {
                    JOptionPane.showMessageDialog(null, "PIN must be 4 digits");
                    return;
                }

                if (!pin1.matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "PIN must contain only numbers");
                    return;
                }

                if (this.cardno == null) {
                    JOptionPane.showMessageDialog(null, "Error: Could not verify current PIN");
                    return;
                }

                try (Connection conn = Connn.getConnection()) {
                    // Update PIN using the card number we got in constructor
                    String updateQuery = "UPDATE login SET pin = ? WHERE cardno = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                        String hashedNewPin = HashUtil.hashPin(pin1);  // Hash only the new PIN
                        updatePs.setString(1, hashedNewPin);
                        updatePs.setString(2, this.cardno);
                        
                        int rowsAffected = updatePs.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "PIN changed successfully");
                            setVisible(false);
                            new main_Class(hashedNewPin);  // Pass the hashed new PIN
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to change PIN. Please try again.");
                        }
                    }
                }
            } else if (e.getSource() == b2) {
                new main_Class(pin);
                setVisible(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred while changing PIN");
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        scaleImage();
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    public static void main(String[] args) {
        new Pin("");
    }
}
