package at.resch.html.test.auth;

import java.sql.SQLException;

import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Partial;
import at.resch.html.elements.BR;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.INPUT;
import at.resch.html.elements.LABEL;
import at.resch.html.elements.P;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

@Partial
@Identifier("login")
public class LoginComponent {

	@Content
	public void makeLoginComponent(HTMLElement html) {
		html.setStyle("text-align: left; max-width: 300px; margin: 0 auto; border: 1px solid #000000; padding: 5px");
		LABEL label_username = new LABEL("Username");
		label_username.setFor("uname");
		html.addObject(label_username);
		INPUT uname = new INPUT();
		uname.setId("uname");
		uname.setType("text");
		html.addObject(uname);
		html.addObject(new BR());
		BUTTON button = new BUTTON("Check");
		button.setOnClickHandler("invokeAction('check_user')");
		html.addObject(button);
		html.addObject(new BR());
		P info = new P();
		info.setId("info");
		html.addObject(info);
	}
	
	@Action(name="check_user", args={"uname"})
	public void actionCheckLogin(Updates updates, String[] args) {
		try {
			DBBackend.init();
			DBBackend.getUserInfo(args[0]);
			updates.addUpdate("info", "User exists");
		} catch (SQLException | NullPointerException e) {
			Session.logger.error("Failed to load UserData", e);
			updates.addUpdate("info", "No such user");
		}
	}

}
