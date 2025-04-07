package Atm.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class main_Class extends JFrame implements ActionListener, ComponentListener {
    JButton b1,b2,b3,b4,b5,b6,b7;
    String pin;
    private JLabel backgroundLabel;
    private ImageIcon originalIcon;
    private Image originalImage;

    main_Class(String pin){
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
        addComponentListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Initial scaling
        scaleImage();
        setVisible(true);
    }

    private void setupComponents() {
        JLabel label = new JLabel("Please Select Your Transaction");
        label.setBounds(430,180,700,35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System",Font.BOLD,28));
        backgroundLabel.add(label);

        b1 = new JButton("DEPOSIT");
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(65,125,128));
        b1.setBounds(410,274,150,35);
        b1.addActionListener(this);
        backgroundLabel.add(b1);

        b2 = new JButton("CASH WITHDRAWL");
        b2.setForeground(Color.WHITE);
        b2.setBackground(new Color(65,125,128));
        b2.setBounds(700,274,150,35);
        b2.addActionListener(this);
        backgroundLabel.add(b2);

        b3 = new JButton("FAST CASH");
        b3.setForeground(Color.WHITE);
        b3.setBackground(new Color(65,125,128));
        b3.setBounds(410,318,150,35);
        b3.addActionListener(this);
        backgroundLabel.add(b3);

        b4 = new JButton("MINI STATEMENT");
        b4.setForeground(Color.WHITE);
        b4.setBackground(new Color(65,125,128));
        b4.setBounds(700,318,150,35);
        b4.addActionListener(this);
        backgroundLabel.add(b4);

        b5 = new JButton("PIN CHANGE");
        b5.setForeground(Color.WHITE);
        b5.setBackground(new Color(65,125,128));
        b5.setBounds(410,362,150,35);
        b5.addActionListener(this);
        backgroundLabel.add(b5);

        b6 = new JButton("BALANCE ENQUIRY");
        b6.setForeground(Color.WHITE);
        b6.setBackground(new Color(65,125,128));
        b6.setBounds(700,362,150,35);
        b6.addActionListener(this);
        backgroundLabel.add(b6);

        b7 = new JButton("EXIT");
        b7.setForeground(Color.WHITE);
        b7.setBackground(new Color(65,125,128));
        b7.setBounds(700,406,150,35);
        b7.addActionListener(this);
        backgroundLabel.add(b7);
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
    public void componentResized(ComponentEvent e) {
        scaleImage();
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==b1){
           new Deposit(pin);
            setVisible(false);
        }else if (e.getSource()==b7){
            System.exit(0);
        } else if (e.getSource()==b2) {
            new Withdrawl(pin);
            setVisible(false);
        } else if (e.getSource()==b6) {
            new BalanceEnquiry(pin);
            setVisible(false);
        } else if (e.getSource()==b3) {
            new FastCash(pin);
            setVisible(false);
        } else if (e.getSource()==b5) {
            new Pin(pin);
            setVisible(false);
        } else if (e.getSource()==b4) {
            new mini(pin);
        }
    }

    public static void main(String[] args) {
        new main_Class("");
    }
}