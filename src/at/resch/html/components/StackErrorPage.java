package at.resch.html.components;

import java.io.PrintWriter;
import java.io.StringWriter;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Page;
import at.resch.html.elements.BR;
import at.resch.html.elements.DIV;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.SPAN;

@Page(title="Fatal Error")
@Identifier("stack-error")
public class StackErrorPage {

	private Throwable t;
	private String target;
	
	public StackErrorPage(Throwable t, String target) {
		this.t = t;
		this.target = target;
	}
	
	@Content
	public void createErrorPage(HTMLElement html) {
		html.setStyle("");
		DIV span = new DIV("Error 500");
		span.setStyle("width: 100%; background: #f00; color: #fff");
		html.addObject(span);
		html.addObject("Some error happened when opening target: " + target);
		html.addObject(new BR());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		DIV div = new DIV();
		String stack = sw.toString();
		div.setStyle("background: light orange");
		stack = stack.replace("\n", "<br />");
		div.addObject(stack);
		html.addObject(div);
	}

}
