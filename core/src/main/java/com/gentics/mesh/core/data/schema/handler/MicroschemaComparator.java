package com.gentics.mesh.core.data.schema.handler;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.gentics.mesh.core.rest.schema.Microschema;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel;

@Singleton
public class MicroschemaComparator extends AbstractFieldSchemaContainerComparator<Microschema> {

	private static MicroschemaComparator instance;

	@Inject
	public MicroschemaComparator() {
		MicroschemaComparator.instance = this;
	}

	public static MicroschemaComparator getIntance() {
		return instance;
	}

	@Override
	public List<SchemaChangeModel> diff(Microschema containerA, Microschema containerB) throws IOException {
		return super.diff(containerA, containerB, Microschema.class);
	}

}
