package Atm.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Deposit extends JFrame implements ActionListener {
   String pin;
   TextField textField;
   JButton b1, b2;
   private JLabel backgroundLabel;
   private ImageIcon originalIcon;
   private Image originalImage;

    Deposit(String pin){
        this.pin = pin;

        // Set window properties
        setLayout(new GridBagLayout());
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
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(new Color(92, 96, 95));  // #5c605f
        
        // Set the background panel to fill the entire frame
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(backgroundPanel, gbc);
        
        // Load original image
        originalIcon = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        originalImage = originalIcon.getImage();
        
        // Create background label
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(null);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(backgroundLabel, gbc);

        // Add components
        setupComponents();
        
        // Set window behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scaleImage();
            }
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Initial scaling
        scaleImage();
        setVisible(true);
    }

    private void setupComponents() {
        JLabel label1 = new JLabel("ENTER AMOUNT YOU WANT TO DEPOSIT");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(460,180,400,35);
        backgroundLabel.add(label1);

        textField = new TextField();
        textField.setBackground(new Color(65,125,128));
        textField.setForeground(Color.WHITE);
        textField.setBounds(460,230,320,25);
        textField.setFont(new Font("Raleway", Font.BOLD,22));
        backgroundLabel.add(textField);

        b1 = new JButton("DEPOSIT");
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
        
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        backgroundLabel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource()==b1){
                String amountText = textField.getText();
                if (amountText.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Please enter the Amount you want to Deposit");
                    return;
                }
                
                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountText);
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Invalid amount format");
                    return;
                }
                
                Date date = new Date();
                
                try (Connection conn = Connn.getConnection();
                     PreparedStatement checkPs = conn.prepareStatement("SELECT 1 FROM bank LIMIT 1")) {
                    checkPs.executeQuery();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database table 'bank' does not exist");
                    return;
                }
                
                Connection conn = Connn.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO bank (pin, date, type, amount) VALUES (?,?,?,?)")) {
                    ps.setString(1, pin);
                    ps.setTimestamp(2, new Timestamp(date.getTime()));
                    ps.setString(3, "Deposit");
                    ps.setBigDecimal(4, amount);
                    
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(null, "Rs. " + amount + " Deposited Successfully");
                    }
                }
                setVisible(false);
                new main_Class(pin);
            } else if (e.getSource()==b2){
                setVisible(false);
                new main_Class(pin);
            }
        } catch (Exception E){
            E.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + E.getMessage());
        }
    }

    public static void main(String[] args) {
        new Deposit("");
    }
}
