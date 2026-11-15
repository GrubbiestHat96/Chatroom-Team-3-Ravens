package ui;
import javax.swing.JButton;

import model.Message;


public class Controller {

JButton btn = new JButton("placeholder");


//tmp msg need main ui to be created to getTxTblock and getUser functionality
Message msg = new Message("placeholder", "sample msg");

public void btnLogin() {


    btn.addActionListener(e -> {
      
        System.out.println("Login button clicked");

    });
}


public void btnCreateAcct() {

    btn.addActionListener(e -> {

        System.out.println("Login button clicked");

    });

}


public void btnSend() {

    btn.addActionListener(e -> {
     
        System.out.println("Login button clicked");

    });
//need code from ui 

}









}


