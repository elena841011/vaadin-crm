package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;

@Route("")
@CssImport("./styles/shared-styles.css")
//@SpringComponent
//@Scope("prototype")
public class MainView extends VerticalLayout {

    private ContactService contactService;
    private Grid<Contact> grid = new Grid<>(Contact.class);
    private TextField filterText = new TextField();
    private ContactForm form;


    public MainView(ContactService contactService, CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();

        configureFilter();
        configureGrid();

//        Initialize the form in the constructor
//        companyService.findAll()
         form = new ContactForm(companyService.findAll());

//        Creates a Div that wraps the grid and the form, gives it a CSS class name, and makes it full size.
//        把grid(左邊的聯絡表格)以及表單填入content中
//        下方有configure grid
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(filterText, content);
        updateList();
    }
    //search by name
    private void configureFilter() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        //Grid就是一個table啊 有row跟column
        grid.setColumns("firstName", "lastName", "email", "status", "company");
        grid.removeColumnByKey("company");

//        定義怎麼從一筆Contact中取得一個column的值
        grid.addColumn(contact -> {
            Company company = contact.getCompany();
            return company == null ? "-" : company.getName();
        }).setHeader("Company");

//        turn on automatic column sizing
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
//        如果沒有輸入search item, 就會找出全部
        grid.setItems(contactService.findAll(filterText.getValue()));
    }

}