package at.resch.html.components.handlers;

public class HKeyEventArgs {

	private int keyCode;

	public HKeyEventArgs(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

}
