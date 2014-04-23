package at.resch.html.components;

import at.resch.html.elements.IMG;

public class HImage extends HComponent<IMG> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6681263549540678207L;

	public HImage(String id, String url) {
		super(new IMG(), id);
		setImageUrl(url);
	}
	
	public void setImageUrl(String url) {
		parent.setSrc(url);
	}
	
	public String getImageUrl(String url) {
		return parent.getSrc();
	}

}
