/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Muhammad Rizwan
 */
@SpringUI
public class MyUI extends UI {

    @Autowired
    private StudentGridView sgv;
    
    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();

        vl.addComponents(sgv);
        setContent(vl);
    }

}
