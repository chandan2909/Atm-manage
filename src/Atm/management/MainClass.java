package Atm.management;

public class MainClass {
    private String pin;
    
    public MainClass(String hashedPin) {  // Rename parameter
        this.pin = hashedPin;  // Store hashed PIN
        // ... rest of constructor ...
    }
    
    public String getPin() {
        return pin;
    }
} 