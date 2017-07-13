/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

@UIScope
@SpringComponent
@SpringUI

public class StudentGridView extends VerticalLayout {

    private FormLayout form;
    private TextField name;
    private TextField email;
    private Button save;
    private Button clear;
    private Student student;
    private Grid<Student> grid;
    private List<Student> list;

    private Binder<Student> binder;
    
    


    @Autowired
    private StudentRepo studentRepo;
    
    public StudentGridView() {
        grid = new Grid<>();
        getGridData();
        name = new TextField("Name :");
        email = new TextField();
        
        
        

//      DataProvider<Student, Void> dataProvider= DataProvider.fromCallbacks(
//                
//                query -> {
//                    // The index of the first item to load
//                    int offset = query.getOffset();
//
//                    // The number of items to load
//                    int limit = query.getLimit();
//                        
//                   return studentRepo.findAll(new PageRequest(offset, limit)).getContent().stream();
//                    
//                  },
//                  // Second callback fetches the number of items for a query
//                  query -> studentRepo.count()
//                );
//        
//        grid.setDataProvider(dataProvider);
        student = new Student();
        binder = new Binder<>(Student.class);
        binder.bindInstanceFields(this);
        binder.readBean(student);

        form = new FormLayout();
        form.setCaption("<h3>Enter Customer Detail...</h3>");
        form.setCaptionAsHtml(true);

        name.setRequiredIndicatorVisible(true);
        name.setIcon(VaadinIcons.USER);
//        name.setWidth("50%");
        name.setPlaceholder("Name...");

        email.setRequiredIndicatorVisible(true);
        email.setCaption("Email :");
//        email.setWidth("50%");
        email.setIcon(VaadinIcons.MAILBOX);
        email.setPlaceholder("Email...");

        save = new Button("Save");
        save.setStyleName(ValoTheme.BUTTON_SMALL);
        save.setDescription("This Button saves and Update Customer Detail");

        save.addClickListener((event) -> {

            try {
                binder.writeBean(student);

            } catch (ValidationException ex) {
                ///TODo something...
            }

            studentRepo.save(student);
            clear();
            getGridData();

        });

        clear = new Button("Clear");
        clear.setStyleName(ValoTheme.BUTTON_SMALL);
        clear.setDescription("Clear all Field to their Default Values(Empty)");
        clear.addClickListener(e -> {
            clear();
            Notification.show("Clear Done", Notification.Type.TRAY_NOTIFICATION);
        });

        form.addComponents(name, email, new HorizontalLayout(clear, save));

        grid.setCaption("<h3>Enter Customer Detail...</h3>");
        grid.setCaptionAsHtml(true);
        grid.setStyleName(ValoTheme.LAYOUT_WELL);
//        grid.setWidth(this.getWidth(), Unit.PERCENTAGE);
        TextField editname = new TextField();
        grid.getEditor().setEnabled(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(Student::getId)
                .setCaption("Id")
                .setId("id");
        grid.addColumn(Student::getName)
                .setEditorComponent(new TextField(), Student::setName)
                .setCaption("Name")
                .setId("name");
        grid.addColumn(Student::getEmail)
                .setEditorComponent(editname, Student::setEmail)
                .setCaption("Email")
                .setId("email");

        grid.getEditor().addSaveListener(e -> {
            student = e.getBean();
            studentRepo.save(student);
        });
//        grid.setWidth("50%");

        HeaderRow filterRow = grid.appendHeaderRow();
        HeaderCell filternamecell = filterRow.getCell("name");
        TextField filter = new TextField();
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.setPlaceholder("Filter name...");
        filternamecell.setComponent(filter);
        filter.addValueChangeListener(e -> {
            listStudent(e.getValue());

        });
        ///Upload file session...
        
        Button uploadbasic = new Button("Go to Basic Upload");
        uploadbasic.addClickListener(e -> {
            removeAllComponents();
            addComponents(new UploadExample("basic"));
        });
        Button uploadadvance = new Button("Go to Advanced Upload");
        uploadadvance.addClickListener(e -> {
            removeAllComponents();
            addComponents(new UploadExample("advanced"));
        });
        ////upload session end...
        Button b=new Button("Generate Report");
         b.addClickListener((event) -> {
        getJasperPrint();
        });
        

        addComponents(new HorizontalLayout(grid,form),new HorizontalLayout( b, uploadbasic, uploadadvance));
        setSizeFull();

    }

    ///Clear all Field to their Default values mean Empty....
    void clear() {
        name.setValue("");
        email.setValue("");

    }

    void getGridData() {
        grid.setDataProvider(
                (sortOrders, offset, limit)
                -> studentRepo.findAll(new PageRequest(offset/limit, limit)).getContent().stream(),
                () -> Integer.parseInt(studentRepo.count() + "")
        );
    }

    ////Method to filter Data on filtering textfield change value..
    void listStudent(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(studentRepo.findAll());
        } else {
            grid.setItems(studentRepo.findByNameStartsWithIgnoreCase(filterText));
        }
    }

    
    ////Method which generates pdf report file of student 
    private List<Map<String, ?>> datasource= new ArrayList<>(); 
    
    private String sourcefile = "studentreport.jrxml";
    private JRDataSource jRDataSource;
    private JasperReport report;
    private JasperPrint filledreport;
    private byte[] bytes;
    
    public void getJasperPrint() {
        for (Student student : studentRepo.findAll()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", student.getId());
            m.put("name", student.getName());
            m.put("email", student.getEmail());
            datasource.add(m);
        }
        jRDataSource = new JRBeanCollectionDataSource(datasource);
        try {
            report = JasperCompileManager.compileReport(sourcefile);
            filledreport = JasperFillManager.fillReport(report, null, jRDataSource);
            bytes=JasperExportManager.exportReportToPdf(filledreport);
           
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream("D:\\upload.pdf",false),1024)) {
            // write a byte sequence
            out.write(bytes);
        } catch (IOException e) {
            Notification.show(e.getMessage(),Notification.Type.WARNING_MESSAGE);
        }
          
            
        } catch (JRException e) {
            Notification.show(e.getMessage());
        }

        
    }
    
    
    
}
