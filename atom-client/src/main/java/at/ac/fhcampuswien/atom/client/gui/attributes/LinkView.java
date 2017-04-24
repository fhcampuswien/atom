package at.ac.fhcampuswien.atom.client.gui.attributes;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;

public class LinkView extends AttributeView<String, LinkView, String> {

	private String prefix, suffix;
	private SimplePanel panel = null;
	private Anchor a = null;
	
	public LinkView(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
		
		a = new Anchor();
		a.setTarget("_blank");
		
		Style anchorStyle = a.getElement().getStyle();
		anchorStyle.setLineHeight(26, Unit.PX);
		
		panel = new SimplePanel(a);
		Style style = panel.getElement().getStyle();
		
		style.setWidth(100, Unit.PCT);
		style.setHeight(26, Unit.PX);
		style.setPaddingLeft(3, Unit.PX);
		style.setFontSize(11, Unit.PX);
		style.setProperty("fontFamily", "Verdana, Arial, Helvetica");
		style.setBorderWidth(1, Unit.PX);
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderColor("rgb(204, 204, 204) rgb(153, 153, 153) rgb(153, 153, 153)");
		style.setProperty("boxSizing", "border-box");
		
		this.initWidget(panel);
	}

	@Override
	protected boolean createFieldWidget() {
		this.field = this;
		return false;
	}

	@Override
	protected void showValue() {
		String link = prefix + value + suffix;
		a.setHref(link);
		a.setText(link);
	}

	@Override
	protected void readValue() {
		//current implementation is read only, so no need to read impossible changes by user
	}
}
