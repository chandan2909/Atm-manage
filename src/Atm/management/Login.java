package Atm.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login extends JFrame implements ActionListener, ComponentListener {
    private static final Logger logger = Logger.getLogger(Login.class.getName());
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private int loginAttempts = 0;
    
    private final JLabel label1, label2, label3;
    private final JTextField cardNumberField;
    private final JPasswordField pinField;
    private final JButton signInButton, clearButton, signUpButton;
    private JLabel background;

    public Login() {
        super("Bank Management System");
        getContentPane().setBackground(new Color(255, 236, 219));

        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not set application icon", e);
        }

        // Initialize components
        label1 = new JLabel("WELCOME TO ATM");
        label2 = new JLabel("Card No:");
        label3 = new JLabel("PIN:");
        cardNumberField = new JTextField(15);
        pinField = new JPasswordField(15);
        signInButton = new JButton("SIGN IN");
        clearButton = new JButton("CLEAR");
        signUpButton = new JButton("SIGN UP");
        
        setupBackground();
        setupComponents();
        setupFrame();
    }

    private void setupBackground() {
        try {
            // Set window size first
            setSize(1550, 830);
            setLocationRelativeTo(null);
            setLayout(null);

            // Load and scale image
            ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
            Image bgImage = bgIcon.getImage();
            
            // Create background label with initial size
            background = new JLabel();
            background.setBounds(0, 0, 1550, 830);
            background.setLayout(null);
            add(background);
            
            // Scale image to fit the window
            Image scaledImage = bgImage.getScaledInstance(1550, 830, Image.SCALE_SMOOTH);
            background.setIcon(new ImageIcon(scaledImage));
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Background image not found, using default background", e);
            background = new JLabel();
            background.setBackground(new Color(255, 236, 219));
            background.setOpaque(true);
            background.setBounds(0, 0, 1550, 830);
            background.setLayout(null);
            add(background);
        }
    }

    private void setupComponents() {
        // Welcome label
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setForeground(new Color(175, 221, 255));  // Black color for better visibility
        label1.setFont(new Font("AvantGarde", Font.BOLD, 38));
        background.add(label1);

        // Card Number field
        label2.setForeground(new Color(175, 221, 255));
        label2.setFont(new Font("Arial", Font.BOLD, 18));
        background.add(label2);

        cardNumberField.setFont(new Font("Arial", Font.PLAIN, 16));
        cardNumberField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        background.add(cardNumberField);

        // PIN field
        label3.setForeground(new Color(175, 221, 255));
        label3.setBackground(new Color(0, 0, 0));
        label3.setFont(new Font("Arial", Font.BOLD, 18));
        background.add(label3);

        pinField.setFont(new Font("Arial", Font.PLAIN, 16));
        pinField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        background.add(pinField);

        // Buttons
        setupButton(signInButton);
        setupButton(clearButton);
        setupButton(signUpButton);
    }

    private void setupButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(65,125,128));  // Blue color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(this);
        background.add(button);
    }

    private void setupFrame() {
        setLayout(null);
        setSize(1550, 830);
        setLocationRelativeTo(null);
        setResizable(true);  // Keep maximize button enabled
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isMaximumSizeSet()) {
                    setMaximumSize(getSize());
                    setMinimumSize(getSize());
                }
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Start maximized
        setVisible(true);
        SwingUtilities.invokeLater(() -> scaleComponents(getSize()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == signInButton) {
                handleSignIn();
            } else if (e.getSource() == clearButton) {
                clearFields();
            } else if (e.getSource() == signUpButton) {
                handleSignUp();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Login action failed", ex);
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.");
        }
    }

    private void handleSignIn() {
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            JOptionPane.showMessageDialog(this, "Too many failed attempts. Please try again later.");
            return;
        }

        String cardNo = cardNumberField.getText().trim();
        String rawPin = new String(pinField.getPassword());

        if (!validateInput(cardNo, rawPin)) {
            return;
        }

        String hashedPin = HashUtil.hashPin(rawPin);
        if (authenticateUser(cardNo, hashedPin)) {
            setVisible(false);
            new main_Class(hashedPin);
        } else {
            loginAttempts++;
            JOptionPane.showMessageDialog(this, 
                String.format("Incorrect Card Number or PIN. Attempts remaining: %d", 
                MAX_LOGIN_ATTEMPTS - loginAttempts));
        }
    }

    private boolean validateInput(String cardNo, String pin) {
        if (cardNo.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Card Number and PIN");
            return false;
        }
        if (!cardNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Card Number must contain only digits");
            return false;
        }
        if (pin.length() < 4) {
            JOptionPane.showMessageDialog(this, "PIN must be at least 4 characters");
            return false;
        }
        return true;
    }

    private boolean authenticateUser(String cardNo, String hashedPin) {
        String query = "SELECT pin FROM login WHERE cardno = ? AND pin = ?";
        try (Connection conn = Connn.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, cardNo);
            ps.setString(2, hashedPin);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Authentication failed", e);
            return false;
        }
    }

    private void clearFields() {
        cardNumberField.setText("");
        pinField.setText("");
    }

    private void handleSignUp() {
        setVisible(false);
        new Signup(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        scaleComponents(getSize());
    }

    private void scaleComponents(Dimension newSize) {
        // Remove all components from background before re-adding them
        background.removeAll();
        
        // Ensure minimum dimensions
        int width = Math.max(newSize.width, 1550);
        int height = Math.max(newSize.height, 830);
        
        // Calculate scaling factors
        double widthScale = width / 1550.0;
        double heightScale = height / 830.0;
        double scale = Math.min(widthScale, heightScale);

        // Update background image
        if (background.getIcon() != null) {
            ImageIcon bgIcon = (ImageIcon) background.getIcon();
            Image scaledImage = bgIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            background.setIcon(new ImageIcon(scaledImage));
            background.setBounds(0, 0, width, height);
        }

        // Welcome label
        int welcomeWidth = (int)(600 * scale);
        int welcomeHeight = (int)(60 * scale);
        int welcomeX = (width - welcomeWidth) / 2;
        label1.setBounds(welcomeX, (int)(100 * scale), welcomeWidth, welcomeHeight);
        background.add(label1);

        // Component dimensions
        int labelWidth = (int)(120 * scale);   // Label width
        int fieldWidth = (int)(350 * scale);   // Field width
        int gap = (int)(15 * scale);           // Gap between label and field
        
        // Center the entire group horizontally
        int centerX = width / 2;
        
        // Vertical positions with better spacing
        int cardY = (int)(250 * scale);
        int pinY = (int)(350 * scale);
        int buttonY = (int)(450 * scale);
        int buttonSpacing = (int)(60 * scale);

        // Card No components - centered
        int cardFieldX = centerX - (fieldWidth / 2);
        label2.setBounds(cardFieldX - labelWidth - gap, cardY, labelWidth, 35);
        cardNumberField.setBounds(cardFieldX, cardY, fieldWidth, 35);
        background.add(label2);
        background.add(cardNumberField);

        // PIN components - centered
        int pinFieldX = centerX - (fieldWidth / 2);
        label3.setBounds(pinFieldX - labelWidth - gap, pinY, labelWidth, 35);
        pinField.setBounds(pinFieldX, pinY, fieldWidth, 35);
        background.add(label3);
        background.add(pinField);

        // Center buttons
        int buttonWidth = (int)(250 * scale);
        int buttonHeight = (int)(40 * scale);
        int buttonX = centerX - (buttonWidth / 2);
        
        signInButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        clearButton.setBounds(buttonX, buttonY + buttonSpacing, buttonWidth, buttonHeight);
        signUpButton.setBounds(buttonX, buttonY + (2 * buttonSpacing), buttonWidth, buttonHeight);
        
        background.add(signInButton);
        background.add(clearButton);
        background.add(signUpButton);
        
        // Force a repaint
        background.revalidate();
        background.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // Empty implementation
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Empty implementation
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Empty implementation
    }

    public static void main(String[] args) {
        LogConfig.configure();
        new Login();
    }
}
