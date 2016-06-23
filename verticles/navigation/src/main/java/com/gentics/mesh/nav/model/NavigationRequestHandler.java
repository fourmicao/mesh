package com.gentics.mesh.nav.model;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentics.mesh.core.data.Tag;
import com.gentics.mesh.core.rest.error.HttpStatusCodeErrorException;
import com.gentics.mesh.error.NodeNotFoundException;
import com.gentics.mesh.etc.MeshSpringConfiguration;

@Component
@Scope("singleton")
public class NavigationRequestHandler implements Handler<RoutingContext> {

	@Autowired
	private MeshSpringConfiguration config;

	private static ForkJoinPool pool = new ForkJoinPool(8);

	private Session session;

	public void handle(RoutingContext rc) {
		this.session = rc.session();
		// LocalizedTag rootTag = tagRepository.findRootTag();
		Tag rootTag = null;
		try {
			Navigation nav = getNavigation(rootTag);
			rc.response().end(toJson(nav));
		} catch (Exception e) {
			throw new HttpStatusCodeErrorException(INTERNAL_SERVER_ERROR, "Could not build naviguation", e);
		}
	}

	private String toJson(Navigation navigation) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(navigation);
	}

	// /**
	// * Returns the mesh auth service which can be used to authenticate resources.
	// *
	// * @return
	// */
	// protected MeshAuthServiceImpl getAuthService() {
	// return config.authService();
	// }

	/**
	 * Returns a content navigation.
	 * 
	 * @param sess
	 * 
	 * @param rootTag
	 * @return
	 * @throws NodeNotFoundException
	 */
	private Navigation getNavigation(Tag rootTag) {
		// TODO handle language
		Navigation nav = new Navigation();
		NavigationElement rootElement = new NavigationElement();
		String name = rootTag.getName();
		rootElement.setName(name);
		rootElement.setType(NavigationElementType.TAG);
		nav.setRoot(rootElement);

		// NavigationTask task = new NavigationTask(rootTag, rootElement, this, genericContentRepository, genericContentUtils);
		// pool.invoke(task);
		return nav;
	}

	// public void canView(GenericNode object, Handler<AsyncResult<Boolean>> resultHandler) {
	// getAuthService().hasPermission(session.getLoginID(), new MeshPermission(object, PermissionType.READ), resultHandler);
	// }

	// /**
	// * Wrapper for the permission checks. Check whether the given object can be viewed by the user.
	// *
	// * @param object
	// * @return true, when the user can view the object. Otherwise false.
	// */
	// public boolean canView(GenericNode object) {
	// return getAuthService().hasPermission(session.id(), new MeshPermission(object, PermissionType.READ));
	// }
}