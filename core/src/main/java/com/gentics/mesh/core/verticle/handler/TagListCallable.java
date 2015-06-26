package com.gentics.mesh.core.verticle.handler;

import java.util.List;

import com.gentics.mesh.core.Page;
import com.gentics.mesh.core.data.model.Tag;
import com.gentics.mesh.core.data.model.node.MeshNode;
import com.gentics.mesh.paging.PagingInfo;

@FunctionalInterface
public interface TagListCallable {

	Page<Tag> findTags(String projectName, MeshNode node, List<String> languageTags, PagingInfo pagingInfo);

}
