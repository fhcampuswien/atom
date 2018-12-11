package at.ac.fhcampuswien.atom.client.gui.attributes;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;

public class LinkView extends AttributeView<String, LinkView, String> {

	private String prefix, suffix;
	private SimplePanel panel = null;
	private Anchor a = null;
	private TextBox textBox = null;
	private Style panelStyle = null;
	
	public LinkView(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
		
		a = new Anchor();
		a.setTarget("_blank");
		Style anchorStyle = a.getElement().getStyle();
		anchorStyle.setLineHeight(26, Unit.PX);
		
		textBox = new TextBox();
		textBox.setValue(value);
		textBox.addStyleName(AtomClientBundle.INSTANCE.css().attributeViewGeneral());
		Style textBoxStyle = textBox.getElement().getStyle();
		textBoxStyle.setHeight(26, Unit.PX);
		textBoxStyle.setPaddingLeft(3, Unit.PX);		
//		textBoxStyle.setWidth(100, Unit.PCT);
		
		
		panel = new SimplePanel(a);
		panelStyle = panel.getElement().getStyle();
	
		
		this.initWidget(panel);
		
		this.field = this;
	}

	@Override
	protected boolean createFieldWidget() {
		return false;
	}

	@Override
	protected void showValue() {
		String link = prefix + value + suffix;
		a.setHref(link);
		a.setText(link);
		textBox.setText(value);
	}

	@Override
	protected void readValue() {
		value = textBox.getText();
		String link = prefix + value + suffix;
		a.setHref(link);
		a.setText(link);
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		panel.clear();
		if(readOnly) {
			panelStyle.setWidth(100, Unit.PCT);
			panelStyle.setHeight(26, Unit.PX);
			panelStyle.setPaddingLeft(3, Unit.PX);
			panelStyle.setFontSize(11, Unit.PX);
			panelStyle.setProperty("fontFamily", "Verdana, Arial, Helvetica");
			panelStyle.setBorderWidth(1, Unit.PX);
			panelStyle.setBorderStyle(BorderStyle.SOLID);
			panelStyle.setBorderColor("rgb(204, 204, 204) rgb(153, 153, 153) rgb(153, 153, 153)");
			panelStyle.setProperty("boxSizing", "border-box");
			panel.add(a);
		}
		else {
			panelStyle.clearPaddingLeft();
			panelStyle.clearBorderWidth();
			panelStyle.clearBorderStyle();
			panelStyle.clearBorderColor();
			panel.add(textBox);
		}
			
	}
}
