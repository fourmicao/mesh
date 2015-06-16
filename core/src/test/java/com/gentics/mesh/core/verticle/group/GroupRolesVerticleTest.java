package com.gentics.mesh.core.verticle.group;

import static com.gentics.mesh.core.data.model.relationship.Permission.READ_PERM;
import static com.gentics.mesh.core.data.model.relationship.Permission.UPDATE_PERM;
import static com.gentics.mesh.util.TinkerpopUtils.count;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.vertx.core.http.HttpMethod;

import java.util.Iterator;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gentics.mesh.core.AbstractRestVerticle;
import com.gentics.mesh.core.data.model.tinkerpop.Group;
import com.gentics.mesh.core.data.model.tinkerpop.Role;
import com.gentics.mesh.core.data.service.GroupService;
import com.gentics.mesh.core.data.service.UserService;
import com.gentics.mesh.core.rest.group.response.GroupResponse;
import com.gentics.mesh.core.rest.role.response.RoleListResponse;
import com.gentics.mesh.core.rest.role.response.RoleResponse;
import com.gentics.mesh.core.verticle.GroupVerticle;
import com.gentics.mesh.test.AbstractRestVerticleTest;
import com.gentics.mesh.util.JsonUtils;

public class GroupRolesVerticleTest extends AbstractRestVerticleTest {

	@Autowired
	private GroupVerticle groupsVerticle;

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserService userService;

	@Override
	public AbstractRestVerticle getVerticle() {
		return groupsVerticle;
	}

	// Group Role Testcases - PUT / Add

	@Test
	public void testReadRolesByGroup() throws Exception {

		Role extraRole = roleService.create("extraRole");
		info.getGroup().addRole(extraRole);
		info.getRole().addPermissions(extraRole, READ_PERM);

		String uuid = info.getGroup().getUuid();
		String response = request(info, HttpMethod.GET, "/api/v1/groups/" + uuid + "/roles", 200, "OK");
		RoleListResponse roleList = JsonUtils.readValue(response, RoleListResponse.class);
		assertEquals(2, roleList.getMetainfo().getTotalCount());
		assertEquals(2, roleList.getData().size());

		Iterator<RoleResponse> roleIt = roleList.getData().iterator();
		RoleResponse roleB = roleIt.next();
		RoleResponse roleA = roleIt.next();
		assertEquals(info.getRole().getUuid(), roleB.getUuid());
		assertEquals(extraRole.getUuid(), roleA.getUuid());
	}

	@Test
	public void testAddRoleToGroup() throws Exception {
		Role extraRole = roleService.create("extraRole");
		info.getRole().addPermissions(extraRole, READ_PERM);

		assertEquals(1, count(info.getGroup().getRoles()));
		String uuid = info.getGroup().getUuid();
		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + uuid + "/roles/" + extraRole.getUuid(), 200, "OK");
		GroupResponse restGroup = JsonUtils.readValue(response, GroupResponse.class);
		assertTrue(restGroup.getRoles().contains("extraRole"));

		Group group = info.getGroup();
		assertEquals(2, count(group.getRoles()));

	}

	@Test
	public void testAddBogusRoleToGroup() throws Exception {
		assertEquals(1, count(info.getGroup().getRoles()));
		String uuid = info.getGroup().getUuid();
		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + uuid + "/roles/" + "bogus", 404, "Not Found");
		expectMessageResponse("object_not_found_for_uuid", response, "bogus");
	}

	@Test
	public void testAddNoPermissionRoleToGroup() throws Exception {
		Role extraRole = roleService.create("extraRole");
		//		try (Transaction tx = graphDb.beginTx()) {
		//Don't grant read permission on role
		//			tx.success();
		//		}

		assertEquals(1, count(info.getGroup().getRoles()));
		String uuid = info.getGroup().getUuid();
		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + uuid + "/roles/" + extraRole.getUuid(), 403, "Forbidden");
		expectMessageResponse("error_missing_perm", response, extraRole.getUuid());

		Group group = info.getGroup();
		assertEquals(1, count(group.getRoles()));
	}

	@Test
	public void testRemoveRoleFromGroup() throws Exception {
		Role extraRole = roleService.create("extraRole");
		info.getGroup().addRole(extraRole);
		info.getRole().addPermissions(extraRole, READ_PERM);
		assertEquals(2, count(info.getGroup().getRoles()));

		String uuid = info.getGroup().getUuid();
		String response = request(info, HttpMethod.DELETE, "/api/v1/groups/" + uuid + "/roles/" + extraRole.getUuid(), 200, "OK");
		GroupResponse restGroup = JsonUtils.readValue(response, GroupResponse.class);
		assertFalse(restGroup.getRoles().contains("extraRole"));
		Group group = info.getGroup();
		assertEquals(1, count(group.getRoles()));

	}

	@Test
	public void testAddRoleToGroupWithPerm() throws Exception {
		Group group = info.getGroup();

		Role extraRole = roleService.create("extraRole");
		info.getRole().addPermissions(extraRole, READ_PERM);

		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + group.getUuid() + "/roles/" + extraRole.getUuid(), 200, "OK");
		System.out.println(response);
		GroupResponse restGroup = JsonUtils.readValue(response, GroupResponse.class);
		test.assertGroup(group, restGroup);

		assertTrue("Role should be assigned to group.", group.hasRole(extraRole));
	}

	@Test
	public void testAddRoleToGroupWithoutPermOnGroup() throws Exception {
		Group group = info.getGroup();

		Role extraRole = roleService.create("extraRole");

		info.getRole().revokePermissions(group, UPDATE_PERM);

		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + group.getUuid() + "/roles/" + extraRole.getUuid(), 403, "Forbidden");
		expectMessageResponse("error_missing_perm", response, group.getUuid());
		assertFalse("Role should not be assigned to group.", group.hasRole(extraRole));
	}

	@Test
	public void testAddRoleToGroupWithBogusRoleUUID() throws Exception {
		Group group = info.getGroup();

		String response = request(info, HttpMethod.POST, "/api/v1/groups/" + group.getUuid() + "/roles/bogus", 404, "Not Found");
		expectMessageResponse("object_not_found_for_uuid", response, "bogus");

	}

	// Group Role Testcases - DELETE / Remove

	@Test
	public void testRemoveRoleFromGroupWithPerm() throws Exception {
		Group group = info.getGroup();

		Role extraRole = roleService.create("extraRole");
		//		try (Transaction tx = graphDb.beginTx()) {
		group.addRole(extraRole);
		//			tx.success();
		//		}

		assertNotNull(group.getUuid());
		assertNotNull(extraRole.getUuid());

		info.getRole().addPermissions(extraRole, READ_PERM);
		info.getRole().addPermissions(group, UPDATE_PERM);

		String response = request(info, HttpMethod.DELETE, "/api/v1/groups/" + group.getUuid() + "/roles/" + extraRole.getUuid(), 200, "OK");
		GroupResponse restGroup = JsonUtils.readValue(response, GroupResponse.class);
		test.assertGroup(group, restGroup);
		assertFalse("Role should now no longer be assigned to group.", group.hasRole(extraRole));
	}

	@Test
	public void testRemoveRoleFromGroupWithoutPerm() throws Exception {
		Group group = info.getGroup();

		Role extraRole = roleService.create("extraRole");
		group.addRole(extraRole);
		info.getRole().revokePermissions(group, UPDATE_PERM);

		String response = request(info, HttpMethod.DELETE, "/api/v1/groups/" + group.getUuid() + "/roles/" + extraRole.getUuid(), 403, "Forbidden");
		expectMessageResponse("error_missing_perm", response, group.getUuid());
		assertTrue("Role should be stil assigned to group.", group.hasRole(extraRole));
	}
}
