package at.resch.html.components;

import at.resch.html.elements.FOOTER;


public class DefaultFooter extends FOOTER {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8613615055009968756L;

	public DefaultFooter() {
		super();
		this.setStyle("position: absolute; height: 60px; bottom: 0;");
	}

	public DefaultFooter(Object... children) {
		super(children);
		this.setStyle("position: absolute; height: 60px; bottom: 0;");
	}

}
