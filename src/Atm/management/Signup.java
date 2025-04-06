package Atm.management;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.border.TitledBorder;
import javax.swing.InputVerifier;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class Signup extends JFrame implements ActionListener {
    // UI Components
    private JTextField textName, textFname, textEmail, textAdd, textCity, textState, textPin;
    private JDateChooser dateChooser;
    private JRadioButton r1, r2, m1, m2, m3;
    private JComboBox<String> comboReligion, comboCategory, comboIncome, comboEducation, comboOccupation;
    private JTextField textPan, textAadhar;
    private JRadioButton seniorYes, seniorNo, existingYes, existingNo;
    private JComboBox<String> comboAccountType;
    private JCheckBox c1, c2, c3, c4, c5, c6;
    private JButton submit;
    
    private final Random ran = new Random();
    private final String formno;
    private final Login loginWindow;

    public Signup(Login login) {
        super("APPLICATION FORM");
        this.loginWindow = login;
        formno = String.format("%04d", ran.nextInt(10000));

        setupUI();
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Start maximized
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loginWindow.setVisible(true);
            }
        });
        setVisible(true);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(47, 76, 96)); // #2f4c60

        // Header panel with improved styling
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setBackground(mainPanel.getBackground());
        ImageIcon bankIcon = new ImageIcon("icon/bank.png");
        JLabel iconLabel = new JLabel(bankIcon);
        headerPanel.add(iconLabel);
        
        JLabel headerLabel = new JLabel("Account Registration Form");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Create a scrollable container for the form sections
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(mainPanel.getBackground());

        // Add sections to form container with proper spacing
        formContainer.add(createPersonalDetailsSection());
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(createAdditionalDetailsSection());
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(createAccountDetailsSection());

        // Create scroll pane with proper settings
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(mainPanel.getBackground());
        scrollPane.getViewport().setBackground(mainPanel.getBackground());
        mainPanel.add(scrollPane);

        // Submit Button Panel with improved styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Add some vertical space before the button
        buttonPanel.add(Box.createVerticalStrut(10));
        
        submit = new JButton("Submit Application");
        submit.setFont(new Font("Arial", Font.BOLD, 16));
        submit.setForeground(Color.WHITE);
        submit.setBackground(new Color(65, 125, 128));
        submit.setFocusPainted(false);
        submit.setBorderPainted(false);
        submit.setPreferredSize(new Dimension(200, 40));
        submit.setMaximumSize(new Dimension(200, 40));
        submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        submit.addActionListener(this);
        buttonPanel.add(submit);
        
        // Add some vertical space after the button
        buttonPanel.add(Box.createVerticalStrut(10));
        
        mainPanel.add(buttonPanel);

        // Add main panel to frame
        add(mainPanel);

        // Set up focus traversal
        setupFocusTraversal();
    }

    private void setupFocusTraversal() {
        textName.addActionListener(_ -> textFname.requestFocus());
        textFname.addActionListener(_ -> dateChooser.getDateEditor().getUiComponent().requestFocus());
        textEmail.addActionListener(_ -> textAdd.requestFocus());
        textAdd.addActionListener(_ -> textCity.requestFocus());
        textCity.addActionListener(_ -> textState.requestFocus());
        textState.addActionListener(_ -> textPin.requestFocus());
        textPin.addActionListener(_ -> textAadhar.requestFocus());
        textAadhar.addActionListener(_ -> seniorYes.requestFocus());
        seniorYes.addActionListener(_ -> seniorNo.requestFocus());
        seniorNo.addActionListener(_ -> existingYes.requestFocus());
        existingYes.addActionListener(_ -> existingNo.requestFocus());
        existingNo.addActionListener(_ -> comboAccountType.requestFocus());
        comboAccountType.addActionListener(_ -> c1.requestFocus());
        c1.addActionListener(_ -> c2.requestFocus());
        c2.addActionListener(_ -> c3.requestFocus());
        c3.addActionListener(_ -> c4.requestFocus());
        c4.addActionListener(_ -> c5.requestFocus());
        c5.addActionListener(_ -> c6.requestFocus());
        c6.addActionListener(_ -> submit.requestFocus());
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1), 
                title,
                TitledBorder.LEFT,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(new Color(47, 76, 96));
        return panel;
    }

    private JPanel createFormRow(String label, Component component) {
        JPanel row = new JPanel(new BorderLayout(15, 5));
        row.setBackground(new Color(47, 76, 96));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 14));
        jLabel.setForeground(Color.WHITE);
        jLabel.setPreferredSize(new Dimension(150, 30));
        
        JPanel componentPanel = new JPanel(new BorderLayout());
        componentPanel.setBackground(new Color(47, 76, 96));
        componentPanel.add(component, BorderLayout.CENTER);
        
        row.add(jLabel, BorderLayout.WEST);
        row.add(componentPanel, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        row.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        return row;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(400, 35));
        field.setMaximumSize(new Dimension(400, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(47, 76, 96));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JTextField createPinField() {
        JTextField field = new JTextField();
        field.setDocument(new LengthRestrictedDocument(6));
        field.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField tf = (JTextField) input;
                return tf.getText().matches("\\d{6}");
            }
        });
        return field;
    }

    private JTextField createAadharField() {
        JTextField field = new JTextField();
        field.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                String newStr = str.replaceAll("[^0-9]", "");
                if ((getLength() + newStr.length()) <= 12) {
                    super.insertString(offs, newStr, a);
                }
            }
        });
        
        field.setPreferredSize(new Dimension(400, 35));
        field.setMaximumSize(new Dimension(400, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(47, 76, 96));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPanel createRadioGroup(String[] options) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(47, 76, 96));
        ButtonGroup group = new ButtonGroup();
        
        for (String opt : options) {
            JRadioButton rb = new JRadioButton(opt);
            rb.setBackground(new Color(47, 76, 96));
            rb.setFont(new Font("Arial", Font.PLAIN, 14));
            rb.setForeground(Color.WHITE);
            group.add(rb);
            panel.add(rb);
            if (opt.equals("Male")) r1 = rb;
            else if (opt.equals("Female")) r2 = rb;
            else if (opt.equals("Married")) m1 = rb;
            else if (opt.equals("Unmarried")) m2 = rb;
            else if (opt.equals("Other")) m3 = rb;
        }
        return panel;
    }

    private JComboBox<String> createCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(400, 35));
        combo.setMaximumSize(new Dimension(400, 35));
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(47, 76, 96));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return combo;
    }

    private JPanel createCheckboxGroup() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 50, 10));  // 2 columns, dynamic rows, 50px horizontal gap, 10px vertical gap
        panel.setBackground(new Color(47, 76, 96));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(600, 120));  // Fixed size to ensure visibility
        
        // Create and add checkboxes
        c1 = createCheckBox("ATM Card");
        c2 = createCheckBox("Internet Banking");
        c3 = createCheckBox("Mobile Banking");
        c4 = createCheckBox("Email Alerts");
        c5 = createCheckBox("Cheque Book");
        c6 = createCheckBox("E-Statement");
        
        // Add all checkboxes in order
        panel.add(c1);
        panel.add(c2);
        panel.add(c3);
        panel.add(c4);
        panel.add(c5);
        panel.add(c6);
        
        return panel;
    }
    
    private JCheckBox createCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBackground(new Color(47, 76, 96));
        cb.setFont(new Font("Arial", Font.PLAIN, 14));
        cb.setForeground(Color.WHITE);
        cb.setPreferredSize(new Dimension(200, 30));  // Fixed size for each checkbox
        return cb;
    }

    private Component createDateField() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.getCalendarButton().setPreferredSize(new Dimension(30, 24));
        dateChooser.setPreferredSize(new Dimension(250, 30));
        dateChooser.setMaximumSize(new Dimension(250, 30));
        return dateChooser;
    }

    private JPanel createSeniorRadioGroup() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(new Color(47, 76, 96));
        ButtonGroup group = new ButtonGroup();
        
        seniorYes = new JRadioButton("Yes");
        seniorNo = new JRadioButton("No");
        
        // Style the radio buttons
        for (JRadioButton rb : new JRadioButton[]{seniorYes, seniorNo}) {
            rb.setBackground(new Color(47, 76, 96));
            rb.setFont(new Font("Arial", Font.PLAIN, 14));
            rb.setForeground(Color.WHITE);
            group.add(rb);
            panel.add(rb);
        }
        
        return panel;
    }

    private JPanel createExistingRadioGroup() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(new Color(47, 76, 96));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        ButtonGroup group = new ButtonGroup();
        
        existingYes = new JRadioButton("Yes");
        existingNo = new JRadioButton("No");
        
        // Style the radio buttons
        for (JRadioButton rb : new JRadioButton[]{existingYes, existingNo}) {
            rb.setBackground(new Color(47, 76, 96));
            rb.setFont(new Font("Arial", Font.PLAIN, 14));
            rb.setForeground(Color.WHITE);
            rb.setPreferredSize(new Dimension(80, 25));
            group.add(rb);
            panel.add(rb);
        }
        
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog loadingDialog = new JDialog(this, "Processing", true);
        loadingDialog.add(new JLabel(new ImageIcon("icon/loading.gif")));
        loadingDialog.setSize(100, 100);
        loadingDialog.setLocationRelativeTo(this);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    if (!validateForm()) return null;

                    // Generate account details
                    String cardno = generateCardNumber();
                    String rawPin = generatePIN();
                    String hashedPin = HashUtil.hashPin(rawPin);

                    // Database operations
                    try (Connection conn = Connn.getConnection()) {
                        conn.setAutoCommit(false);
                        
                        // Insert into signup
                        try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO signup (formno, name, fname, dob, gender, email, marital, address, city, state, pin, religion, category, income, education, occupation, pan, aadhar, senior, existing, account_type, facilities) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                            ps.setString(1, formno);
                            ps.setString(2, textName.getText());
                            ps.setString(3, textFname.getText());
                            ps.setDate(4, new java.sql.Date(dateChooser.getDate().getTime()));
                            ps.setString(5, getSelectedRadio("Gender"));
                            ps.setString(6, textEmail.getText());
                            ps.setString(7, getSelectedRadio("Marital"));
                            ps.setString(8, textAdd.getText());
                            ps.setString(9, textCity.getText());
                            ps.setString(10, textState.getText());
                            ps.setString(11, textPin.getText());
                            ps.setString(12, (String) comboReligion.getSelectedItem());
                            ps.setString(13, (String) comboCategory.getSelectedItem());
                            ps.setString(14, (String) comboIncome.getSelectedItem());
                            ps.setString(15, (String) comboEducation.getSelectedItem());
                            ps.setString(16, (String) comboOccupation.getSelectedItem());
                            ps.setString(17, textPan.getText());
                            ps.setString(18, textAadhar.getText().replaceAll(",", ""));
                            ps.setString(19, getSelectedRadio("Senior"));
                            ps.setString(20, getSelectedRadio("Existing"));
                            ps.setString(21, (String) comboAccountType.getSelectedItem());
                            ps.setString(22, getSelectedFacilities());
                            ps.executeUpdate();
                        }

                        // Insert into login
                        try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO login VALUES (?,?,?)")) {
                            ps.setString(1, formno);
                            ps.setString(2, cardno);
                            ps.setString(3, hashedPin);
                            ps.executeUpdate();
                        }

                        conn.commit();
                        
                        // Show success dialog with card number and PIN
                        SwingUtilities.invokeLater(() -> {
                            JPanel successPanel = new JPanel();
                            successPanel.setLayout(new BoxLayout(successPanel, BoxLayout.Y_AXIS));
                            successPanel.setBackground(new Color(47, 76, 96));
                            successPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
                            
                            JLabel successLabel = new JLabel("Registration Successful!");
                            successLabel.setFont(new Font("Arial", Font.BOLD, 28));
                            successLabel.setForeground(Color.WHITE);
                            successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            
                            JLabel cardLabel = new JLabel("Your Card Number: " + cardno);
                            cardLabel.setFont(new Font("Arial", Font.BOLD, 18));
                            cardLabel.setForeground(new Color(175, 221, 255));
                            cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            
                            JLabel pinLabel = new JLabel("Your PIN: " + rawPin);
                            pinLabel.setFont(new Font("Arial", Font.BOLD, 18));
                            pinLabel.setForeground(new Color(175, 221, 255));
                            pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            
                            JLabel noteLabel = new JLabel("Please note these details for future login");
                            noteLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                            noteLabel.setForeground(new Color(255, 200, 200));
                            noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            
                            successPanel.add(Box.createVerticalStrut(20));
                            successPanel.add(successLabel);
                            successPanel.add(Box.createVerticalStrut(30));
                            successPanel.add(cardLabel);
                            successPanel.add(Box.createVerticalStrut(15));
                            successPanel.add(pinLabel);
                            successPanel.add(Box.createVerticalStrut(30));
                            successPanel.add(noteLabel);
                            successPanel.add(Box.createVerticalStrut(20));
                            
                            // Create custom JOptionPane with styled background
                            JOptionPane optionPane = new JOptionPane(
                                successPanel,
                                JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.DEFAULT_OPTION,
                                null,
                                new Object[]{},
                                null
                            );
                            
                            // Create and style the dialog
                            JDialog dialog = optionPane.createDialog(Signup.this, "Registration Complete");
                            dialog.setBackground(new Color(47, 76, 96));
                            dialog.setSize(500, 400);
                            dialog.setLocationRelativeTo(Signup.this);
                            
                            // Add a styled OK button
                            JButton okButton = new JButton("OK");
                            okButton.setFont(new Font("Arial", Font.BOLD, 16));
                            okButton.setForeground(Color.WHITE);
                            okButton.setBackground(new Color(65, 125, 128));
                            okButton.setFocusPainted(false);
                            okButton.setBorderPainted(false);
                            okButton.setPreferredSize(new Dimension(100, 35));
                            okButton.addActionListener(_ -> dialog.dispose());
                            
                            JPanel buttonPanel = new JPanel();
                            buttonPanel.setBackground(new Color(47, 76, 96));
                            buttonPanel.add(okButton);
                            successPanel.add(buttonPanel);
                            
                            dialog.setVisible(true);
                            
                            loginWindow.setVisible(true);
                            dispose();
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            Signup.this, 
                            "Error: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    });
                    ex.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }

    private boolean validateForm() {
        List<String> errors = new ArrayList<>();
        
        // Required field validations
        if (textName.getText().trim().isEmpty()) errors.add("Name is required");
        if (textFname.getText().trim().isEmpty()) errors.add("Father's Name is required");
        if (dateChooser.getDate() == null) errors.add("Date of Birth is required");
        if (!r1.isSelected() && !r2.isSelected()) errors.add("Gender is required");
        if (textEmail.getText().trim().isEmpty()) errors.add("Email is required");
        if (!m1.isSelected() && !m2.isSelected() && !m3.isSelected()) errors.add("Marital Status is required");
        if (textAdd.getText().trim().isEmpty()) errors.add("Address is required");
        if (textCity.getText().trim().isEmpty()) errors.add("City is required");
        if (textState.getText().trim().isEmpty()) errors.add("State is required");
        if (textPin.getText().length() != 6) errors.add("PIN Code must be 6 digits");
        if (textPan.getText().trim().isEmpty()) errors.add("PAN Number is required");
        if (textAadhar.getText().trim().isEmpty()) errors.add("Aadhar Number is required");
        if (!seniorYes.isSelected() && !seniorNo.isSelected()) errors.add("Senior Citizen status is required");
        if (!existingYes.isSelected() && !existingNo.isSelected()) errors.add("Existing Account status is required");
        
        // Format validations
        if (!textEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) errors.add("Invalid email format");
        if (!textAadhar.getText().matches("\\d{12}")) errors.add("Aadhar Number must be 12 digits");
        
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "<html><b>Validation Errors:</b><br>" + 
                String.join("<br>• ", errors) + "</html>", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String getSelectedRadio(String type) {
        switch (type) {
            case "Gender": return getSelected(r1, r2);
            case "Marital": return getSelected(m1, m2, m3);
            case "Senior": return getSelected(seniorYes, seniorNo);
            case "Existing": return getSelected(existingYes, existingNo);
            default: return null;
        }
    }

    private String getSelected(JRadioButton... buttons) {
        for (JRadioButton btn : buttons) {
            if (btn != null && btn.isSelected()) return btn.getText();
        }
        return null;
    }

    private String getSelectedFacilities() {
        StringBuilder sb = new StringBuilder();
        if (c1.isSelected()) sb.append("ATM Card,");
        if (c2.isSelected()) sb.append("Internet Banking,");
        if (c3.isSelected()) sb.append("Mobile Banking,");
        if (c4.isSelected()) sb.append("Email Alerts,");
        if (c5.isSelected()) sb.append("Cheque Book,");
        if (c6.isSelected()) sb.append("E-Statement");
        return sb.toString().replaceAll(",$", "");
    }

    private String generateCardNumber() {
        return String.format("%016d", Math.abs(ran.nextLong()) % 10000000000000000L);
    }

    private String generatePIN() {
        return String.format("%04d", ran.nextInt(10000));
    }

    private JPanel createPersonalDetailsSection() {
        JPanel personalPanel = createSectionPanel("Personal Details");
        personalPanel.setPreferredSize(new Dimension(800, 500));
        personalPanel.setMaximumSize(new Dimension(800, 500));
        
        // Create a panel for the form fields with proper spacing
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(new Color(47, 76, 96));
        
        // Add form rows with consistent spacing
        fieldsPanel.add(createFormRow("Name:", textName = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Father's Name:", textFname = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Date of Birth:", createDateField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Gender:", createRadioGroup(new String[]{"Male", "Female"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Email:", textEmail = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Marital Status:", createRadioGroup(new String[]{"Married", "Unmarried", "Other"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Address:", textAdd = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("City:", textCity = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("State:", textState = createTextField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("PIN Code:", textPin = createPinField()));
        
        personalPanel.add(fieldsPanel);
        return personalPanel;
    }

    private JPanel createAdditionalDetailsSection() {
        JPanel additionalPanel = createSectionPanel("Additional Details");
        additionalPanel.setPreferredSize(new Dimension(800, 500));
        additionalPanel.setMaximumSize(new Dimension(800, 500));
        
        // Create a panel for the form fields with proper spacing
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(new Color(47, 76, 96));
        
        // Add form rows with consistent spacing
        fieldsPanel.add(createFormRow("Religion:", comboReligion = createCombo(new String[]{"Hindu","Muslim","Sikh","Christian","Other"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Category:", comboCategory = createCombo(new String[]{"General","OBC","SC","ST","Other"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Income:", comboIncome = createCombo(new String[]{"<1.5L","<2.5L","<5L","<10L",">10L"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Education:", comboEducation = createCombo(new String[]{"Non-Graduate","Graduate","Post-Graduate","Doctorate"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Occupation:", comboOccupation = createCombo(new String[]{"Salaried","Self-Employed","Business","Student","Retired"})));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("PAN Number:", textPan = createPanField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Aadhar Number:", textAadhar = createAadharField()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Senior Citizen:", createSeniorRadioGroup()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFormRow("Existing Account:", createExistingRadioGroup()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        additionalPanel.add(fieldsPanel);
        return additionalPanel;
    }

    private JPanel createAccountDetailsSection() {
        JPanel accountPanel = createSectionPanel("Account Details");
        accountPanel.setPreferredSize(new Dimension(800, 200));
        accountPanel.setMaximumSize(new Dimension(800, 200));
        
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(new Color(47, 76, 96));
        
        fieldsPanel.add(createFormRow("Account Type:", comboAccountType = createCombo(new String[]{"Saving","Fixed Deposit","Current","Recurring Deposit"})));
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(createFormRow("Facilities:", createCheckboxGroup()));
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        accountPanel.add(fieldsPanel);
        return accountPanel;
    }

    private JTextField createPanField() {
        JTextField field = new JTextField();
        field.setDocument(new LengthRestrictedDocument(10));
        
        field.setPreferredSize(new Dimension(400, 35));
        field.setMaximumSize(new Dimension(400, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(47, 76, 96));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return field;
    }
}

class LengthRestrictedDocument extends PlainDocument {
    private final int limit;
    public LengthRestrictedDocument(int limit) {
        this.limit = limit;
    }
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (getLength() + str.length() <= limit) {
            super.insertString(offs, str, a);
        }
    }
}
