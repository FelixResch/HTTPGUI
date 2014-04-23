package at.resch.html.extensions;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Partial;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.P;
import at.resch.html.elements.PROGRESS;

@Partial
public class JobProgress {

	private String display;
	
	private int count;
	private int of;
	private double percent;

	public JobProgress(String display, int count, int of) {
		percent = (double) count / (double) of * 100;
		this.count = count;
		this.of = of;
		this.display = display;
	}

	public JobProgress(String display, int count, double percent) {
		of = (int) ((double) count / percent * 100.d);
		this.percent = percent;
		this.count = count;
		this.display = display;
	}

	public JobProgress(String display, double percent, int of) {
		count = (int) ((double) of * percent / 100.d);
		this.percent = percent;
		this.of = of;
		this.display = display;
	}

	public JobProgress(String display, double percent) {
		count = -1;
		of = -1;
		this.percent = percent;
		this.display = display;
	}
	
	@Content
	public void createHtml(HTMLElement html) {
		html.setStyle("border: 0.5px solid #dddddd; text-align: center");
		String prog = display + " (";
		if(count == -1) {
			prog += percent + " %";
		} else {
			prog += count + " / " + of;
		}
		prog += ")";
		html.addObject(new P(prog));
		PROGRESS progress = new PROGRESS();
		progress.setMax("100");
		progress.setValue("" + percent);
		html.addObject(progress);
	}

}
