package com.gentics.mesh.core.data.model.node.field.impl.basic;

import com.gentics.mesh.core.data.model.impl.AbstractFieldContainerImpl;
import com.gentics.mesh.core.data.model.node.field.basic.AbstractBasicField;
import com.gentics.mesh.core.data.model.node.field.basic.DateField;

public class DateFieldImpl extends AbstractBasicField implements DateField{

	public DateFieldImpl(String fieldKey, AbstractFieldContainerImpl parentContainer) {
		super(fieldKey, parentContainer);
	}

	public void setDate(String date) {
		setFieldProperty("date", date);
	}

	public String getDate() {
		return getFieldProperty("date");
	}
}
