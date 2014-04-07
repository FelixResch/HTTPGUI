package at.resch.html.test.auth;

import java.sql.SQLException;

import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.annotations.Priority;
import at.resch.html.components.Info;
import at.resch.html.components.Success;
import at.resch.html.elements.BR;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.INPUT;
import at.resch.html.elements.LABEL;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

@Partial
@Priority
@Identifier("register")
public class RegisterPage {
	
	@Content
	public void createRegisterForm(HTMLElement html) {
		html.setStyle("margin: 0 auto; border: 1px solid #000; padding: 5px; width: 30%; text-align: center");
		html.addObject(new H1("Register User"));
		LABEL label = new LABEL("Username");
		label.setFor("uname");
		html.addObject(label);
		INPUT uname = new INPUT();
		uname.setType("text");
		uname.setId("uname");
		html.addObject(uname);
		LABEL label_pass = new LABEL("Password");
		label_pass.setFor("pass");
		html.addObject(label_pass);
		INPUT pass = new INPUT();
		pass.setType("password");
		pass.setId("pass");
		html.addObject(pass);
		LABEL label_display = new LABEL("Display Name");
		label_display.setFor("display");
		html.addObject(label_display);
		INPUT display = new INPUT();
		display.setType("text");
		display.setId("display");
		html.addObject(display);
		LABEL label_email = new LABEL("E-Mail");
		label_email.setFor("email");
		html.addObject(label_email);
		INPUT email = new INPUT();
		email.setId("email");
		email.setType("email");
		html.addObject(email);
		html.addObject(new BR());
		BUTTON button = new BUTTON("Register");
		button.setOnClickHandler("invokeAction('reg_user')");
		html.addObject(button);
		html.addObject("   ");
		BUTTON back = new BUTTON("Back");
		back.setOnClickHandler("invokeAction('reg_back')");
		html.addObject(back);
	}
	
	@Action(name="reg_user", args={"uname", "pass", "display", "email"})
	public void actionRegister(Updates updates, String[] args) {
		String uname = args[0];
		String pass = args[1];
		String display = args[2];
		String email = args[3];
		try {
			DBBackend.init();
		} catch (Exception e) {
			updates.addUpdate("messages", new at.resch.html.components.Error("Error registering User!").renderHTML());
		}
		if(DBBackend.registerUser(uname, pass, email, display)) 
			updates.addUpdate("messages", new Success("Succesfully registered User!").renderHTML());
		else
			updates.addUpdate("messages", new at.resch.html.components.Error("Error registering User!").renderHTML());
		updates.addUpdate("page_main", Session.getCompiledPartial("main_cont").renderHTML());
		updates.addUpdate("title", "User Authentication");
	}
	
	@Action(name="reg_back", args={})
	public void back(Updates updates) {
		updates.addUpdate("page_main", Session.getCompiledPartial("main_cont").renderHTML());
		updates.addUpdate("title", "User Authentication");
	}
}
