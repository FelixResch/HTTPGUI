package at.resch.html.components;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Page;
import at.resch.html.elements.BR;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.TABLE;
import at.resch.html.elements.TD;
import at.resch.html.elements.TR;
import at.resch.html.server.Session;

@Page(title="Fehler")
@Identifier("sep")
public class StandardErrorPage {

	private int errNo = 404;
	private String target;
	
	public StandardErrorPage(String target) {
		this.target = target;
	}
	
	public StandardErrorPage(int errNo, String target) {
		this.errNo = errNo;
		this.target = target;
	}
	
	@Content
	public void renderErrorPage(HTMLElement html) {
		html.addObject(new H1("Fehler " + errNo));
		html.addObject("Der Server kann die angegebene Datei nicht finden: " + target);
		html.addObject(Session.getCompiledPartial("info"));
		html.addObject(new BR());
		html.addObject(Session.getCompiledPartial("footer"));
//		html.setStyle("font-family: monospaced; font-size: 24pt; background: #0000ff; color: #ffffff");
//		html.addObject("*** STOP: (0x" + errNo + ")");
//		html.addObject(new BR());
//		html.addObject("PAGE_NOT_FOUND" + target.toUpperCase().replace("/", "_"));
//		html.addObject(new BR());
//		html.addObject("SERVER: HTTTPGUI SERVER 0.1 BETA");
//		html.addObject(new BR());
//		TABLE table = new TABLE();
//		table.setStyle("border: none");
//		for(int i = 0; i < 10; i++) {
//			TR tr = new TR();
//			tr.setStyle("padding: 0px;");
//			for(int j = 0; j < 3; j++) {
//				TD td = new TD();
//				td.setStyle("border: none; font-size: 24pt;padding: 0px");
//				for(int k = 0; k < 25; k++) {
//					td.addObject(("" + ((char)(Math.random() * 26 + 65))).toUpperCase());
//				}
//				tr.addObject(td);
//			}
//			table.addObject(tr);
//		}
//		html.addObject(table);
	}

}
 