package com.example.controller;

import org.springframework.stereotype.Component;

@Component
public class ControllerChars {

    private Controller controller;

    public void initialize() {
        System.out.println("ControllerChars initialized");
    }

    public void setController(Controller controller) {
            this.controller = controller;
    }
    
}
