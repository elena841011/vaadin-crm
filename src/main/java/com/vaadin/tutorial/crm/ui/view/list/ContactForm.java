package com.vaadin.tutorial.crm.ui.view.list;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;

import java.util.List;

public class ContactForm extends FormLayout {

	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	EmailField email = new EmailField("Email");
	ComboBox<Contact.Status> status = new ComboBox<>("Status");
	ComboBox<Company> company = new ComboBox<>("Company");
	Button save = new Button("Save");
	Button delete = new Button("Delete");
	Button close = new Button("Cancel");

	private Contact contact;

//    With these two lines of code, you’ve made the UI fields ready to be connected to a contact.
	Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

//    要加上這個，因為下面的ContactForm加了參數了
//    沒用到就不用加
//    ContactForm() {
//
//    }

//    List<Company> companies

	public void setContact(Contact contact) {
		this.contact = contact;

//        bind the values from the contact to the UI fields.
//        One-way data binding. Sets values from the bean to the input fields:
		binder.readBean(contact);
	}

	public ContactForm(List<Company> companyService) {

		addClassName("contact-form");
//        bindInstanceFields matches fields in Contact and ContactForm based on their names.
		binder.bindInstanceFields(this);

		company.setItems(companyService);

//        Tells the combo box to use the name of the company as the display value.
//        Company::getName-> method reference

//        從item長出label, 若不設定的話就會call to String = String::valueOf
		company.setItemLabelGenerator(Company::getName);
		status.setItems(Contact.Status.values());

		// add placeHolder
		firstName.setPlaceholder("Please enter your name");

		add(firstName, lastName, email, company, status,
//                The buttons require a bit of extra configuration
				createButtonsLayout());
	}

	private HorizontalLayout createButtonsLayout() {
//        Makes the buttons visually distinct from each other using built-in theme variants.
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

//        Defines keyboard shortcuts: Enter to save and Escape to close the editor
		save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);

		save.addClickListener(event -> validateAndSave());
		delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact)));
		close.addClickListener(event -> fireEvent(new CloseEvent(this)));

//		Validates the form every time it changes. If it is invalid, it disables the save button to avoid invalid submissions.
		binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

		return new HorizontalLayout(save, delete, close);
	}

	private void validateAndSave() {
		try {
			binder.writeBean(contact);
			fireEvent(new SaveEvent(this, contact));
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

// Events
	public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
		private Contact contact;

		protected ContactFormEvent(ContactForm source, Contact contact) {
			super(source, false);
			this.contact = contact;
		}

		public Contact getContact() {
			return contact;
		}
	}

	public static class SaveEvent extends ContactFormEvent {
		SaveEvent(ContactForm source, Contact contact) {
			super(source, contact);
		}
	}

	public static class DeleteEvent extends ContactFormEvent {
		DeleteEvent(ContactForm source, Contact contact) {
			super(source, contact);
		}

	}

	public static class CloseEvent extends ContactFormEvent {
		CloseEvent(ContactForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}

}