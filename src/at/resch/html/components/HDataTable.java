package at.resch.html.components;

import at.resch.html.annotations.Restoreable;
import at.resch.html.components.models.TableModel;
import at.resch.html.elements.TABLE;
import at.resch.html.elements.TD;
import at.resch.html.elements.TH;
import at.resch.html.elements.TR;

@Restoreable
public class HDataTable extends HComponent<TABLE>{

	private TableModel model;
	
	public HDataTable(String id, TableModel model) {
		super(new TABLE(), id);
		this.setModel(model);
		setStyleClass("table table-striped table-bordered table-hover");
		renderTable();
	}

	public TableModel getModel() {
		return model;
	}

	public void setModel(TableModel model) {
		this.model = model;
		renderTable();
	}
	
	public void revalidate() {
		renderTable();
	}
	
	protected void renderTable() {
		parent.clearChildren();
		String[] header = model.getHeaders();
		TR headrs = new TR();
		for(int i = 0; i < header.length; i++) {
			TH td = new TH(header[i]);
			headrs.addObject(td);
		}
		addObject(headrs);
		String[][] data = model.getData();
		for(int i = 0; i < data.length; i++) {
			TR tr = new TR();
			for(int j = 0; j < data[i].length; j++) {
				TD td = new TD(data[i][j]);
				tr.addObject(td);
			}
			addObject(tr);
		}
	}
}
