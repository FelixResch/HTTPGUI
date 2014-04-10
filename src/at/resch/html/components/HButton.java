package at.resch.html.components;

import at.resch.html.elements.A;

public class HButton extends HComponent<A>{

	public static final String BUTTON_LARGE = "btn-large";
	public static final String BUTTON_SMALL = "btn-small";
	public static final String BUTTON_MINI = "btn-mini";
	
	public static final String BUTTON_PRIMARY = "btn-primary";
	public static final String BUTTON_WARNING = "btn-warning";
	public static final String BUTTON_DANGER = "btn-danger";
	public static final String BUTTON_SUCCESS = "btn-success";
	public static final String BUTTON_INFO = "btn-primary";
	public static final String BUTTON_INVERSE = "btn-primary";
	
	private String buttonSize = "";
	private String buttonType = "";
	
	public HButton(String id) {
		super(new A(), id);
		setStyleClass("btn");
	}
	

	public String getButtonSize() {
		return buttonSize;
	}

	public void setButtonSize(String buttonSize) {
		this.buttonSize = buttonSize;
		updateStyleClasses();
	}

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
		updateStyleClasses();
	}
	
	protected void updateStyleClasses() {
		setStyleClass("btn" + " " + buttonType + " " + buttonSize);
	}

}
