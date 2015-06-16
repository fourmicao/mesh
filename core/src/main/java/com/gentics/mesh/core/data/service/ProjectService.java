package com.gentics.mesh.core.data.service;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gentics.mesh.core.Page;
import com.gentics.mesh.core.data.model.root.ProjectRoot;
import com.gentics.mesh.core.data.model.tinkerpop.Project;
import com.gentics.mesh.core.data.model.tinkerpop.User;
import com.gentics.mesh.core.rest.project.response.ProjectResponse;
import com.gentics.mesh.paging.PagingInfo;
import com.gentics.mesh.util.InvalidArgumentException;
import com.gentics.mesh.util.PagingHelper;
import com.syncleus.ferma.traversals.VertexTraversal;

@Component
public class ProjectService extends AbstractMeshService {

	private static Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	protected UserService userService;

	public Project findByName(String projectName) {
		return framedGraph.v().has("name", projectName).nextExplicit(Project.class);
	}

	public Project findByUUID(String uuid) {
		return framedGraph.v().has("uuid", uuid).nextExplicit(Project.class);
	}

	public List<? extends Project> findAll() {
		return framedGraph.v().has(Project.class).toListExplicit(Project.class);
	}

	public void deleteByName(String name) {
	}

	public ProjectResponse transformToRest(RoutingContext rc, Project project) {
		ProjectResponse projectResponse = new ProjectResponse();
		projectResponse.setUuid(project.getUuid());
		projectResponse.setName(project.getName());
		projectResponse.setPerms(userService.getPerms(rc, project));

		//		MeshNode rootNode = neo4jTemplate.fetch(project.getRootNode());
		//		if (rootNode != null) {
		//			projectResponse.setRootNodeUuid(rootNode.getUuid());
		//		} else {
		//			log.info("Inconsistency detected. Project {" + project.getUuid() + "} has no root node.");
		//		}
		//		return projectResponse;
		return null;
	}

	public Page<? extends Project> findAllVisible(User requestUser, PagingInfo pagingInfo) throws InvalidArgumentException {
		//	@Query(value = "MATCH (requestUser:User)-[:MEMBER_OF]->(group:Group)<-[:HAS_ROLE]-(role:Role)-[perm:HAS_PERMISSION]->(project:Project) where id(requestUser) = {0} and perm.`permissions-read` = true return project ORDER BY project.name", countQuery = "MATCH (requestUser:User)-[:MEMBER_OF]->(group:Group)<-[:HAS_ROLE]-(role:Role)-[perm:HAS_PERMISSION]->(project:Project) where id(requestUser) = {0} and perm.`permissions-read` = true return count(project)")
		//TODO check whether it is faster to use meshroot for starting the traversal
		VertexTraversal traversal = framedGraph.v().has(ProjectRoot.class);
		return PagingHelper.getPagedResult(traversal, pagingInfo, Project.class);

	}

	public ProjectRoot findRoot() {
		return framedGraph.v().has(ProjectRoot.class).nextExplicit(ProjectRoot.class);
	}

	//	@Override
	//	public Project save(Project project) {
	//		ProjectRoot root = projectRepository.findRoot();
	//		if (root == null) {
	//			throw new NullPointerException("The project root node could not be found.");
	//		}
	//		project = neo4jTemplate.save(project);
	//		root.getProjects().add(project);
	//		neo4jTemplate.save(root);
	//		return project;
	//		return null;
	//	}

	public Project create(String name) {
		Project project = framedGraph.addFramedVertex(Project.class);
		project.setName(name);
		return project;
	}

	public ProjectRoot createRoot() {
		return framedGraph.addFramedVertex(ProjectRoot.class);
	}

	public void delete(Project project) {
		project.getVertex().remove();
	}

}
