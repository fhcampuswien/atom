/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.slider.SliderBar;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Window;

public class SliderView extends AttributeView<Double, SliderBar, Double> {

	private static final int pixelWidthSubtraction = 420;

	private double defaultValue, minValue, maxValue;
	private int roundTo;
	private boolean valueSet = false;

	public SliderView(double minValue, double maxValue, double defaultValue, double stepSize, int roundTo) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.roundTo = roundTo;
		// this.stepSize = stepSize;
		this.defaultValue = defaultValue;

		field.setMinValue(minValue);
		field.setMaxValue(maxValue);
		field.setStepSize(stepSize);

		// field.addValueChangeHandler(new ValueChangeHandler<Double>() {
		//
		// @Override
		// public void onValueChange(ValueChangeEvent<Double> event) {
		// System.out.println(event.getValue());
		// }
		//
		// });
	}

	/**
	 * @see at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView#readValue()
	 */
	@Override
	protected void readValue() {
		Double sliderValue = field.getValue();
		if (sliderValue == null) {
			value = null;
			return;
		}
		if (!sliderValue.isInfinite() && !sliderValue.isNaN() && sliderValue >= minValue && sliderValue <= maxValue) {
			if (roundTo >= 0)
				value = AtomTools.roundToDecimals(sliderValue, roundTo);
			else
				value = sliderValue;
		} else {
			value = null;
		}

		AtomTools.log(Log.LOG_LEVEL_TRACE, "read value: " + sliderValue + "; stored value: " + value, this);
		// if(value.equals(Double.NaN))
		// value = null;
	}

	@Override
	protected boolean createFieldWidget() {
		this.field = new SliderBar(0, 0);
		field.setHeight("30px");
		field.setNumLabels(10);
		field.setNumTicks(20);
		
		field.addValueChangeHandler(getVCHandler());

		resize(Window.getClientWidth() - pixelWidthSubtraction);

		return true;
	}

	@Override
	protected void showValue() {
		if (valueSet || value != null) {
			field.setCurrentValue(value != null ? value : defaultValue);
			valueSet = true;
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		field.setEnabled(!readOnly);
	}

	@Override
	public void resize(ResizeEvent event) {
		if (event != null)
			resize(event.getWidth() - pixelWidthSubtraction);
	}

	private int lastWidth;

	private void resize(int width) {
		if (lastWidth > 0 && width < (lastWidth - 50)) {
			App.requestSecondResize();
		}
		lastWidth = width;

		// field.setWidth("" + width + "px");
		field.setWidth("100%");
		field.redraw();
		AtomTools.log(Log.LOG_LEVEL_TRACE, "resizing SliderView to " + width, this);
	}
}
