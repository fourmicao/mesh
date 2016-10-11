package com.gentics.mesh.core.rest.user;

import com.gentics.mesh.core.rest.schema.SchemaReference;

public class NodeReferenceImpl implements NodeReference {

	private String projectName;
	private String uuid;
	private String displayName;
	private String path;
	private SchemaReference schema;

	@Override
	public String getUuid() {
		return uuid;
	}

	/**
	 * Set the node uuid.
	 * 
	 * @param uuid
	 *            Uuid of the node
	 */
	public NodeReferenceImpl setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public String getPath() {
		return path;
	}

	public NodeReferenceImpl setPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Return the project name.
	 * 
	 * @return Name of the project to which the node belongs
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Set the project name.
	 * 
	 * @param projectName
	 *            Name of the project to which the node belongs
	 */
	public NodeReferenceImpl setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	/**
	 * Return the display name.
	 * 
	 * @return Display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name.
	 * 
	 * @param displayName
	 *            Display name
	 */
	public NodeReferenceImpl setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * Return the schema reference.
	 * 
	 * @return Schema reference of the node
	 */
	public SchemaReference getSchema() {
		return schema;
	}

	/**
	 * Set the schema reference.
	 * 
	 * @param schema
	 *            Schema reference for the node
	 */
	public NodeReferenceImpl setSchema(SchemaReference schema) {
		this.schema = schema;
		return this;
	}

}
