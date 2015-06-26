package com.gentics.mesh.core.data.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.gentics.mesh.core.data.model.Language;
import com.gentics.mesh.core.data.model.root.LanguageRoot;

@Component
public class LanguageService extends AbstractMeshService {

	public static LanguageService instance;

	@PostConstruct
	public void setup() {
		instance = this;
	}

	public static LanguageService getLanguageService() {
		return instance;
	}

	public Language findByName(String name) {
		return fg.v().has("name", name).nextOrDefault(Language.class, null);
	}

	/**
	 * Find the language with the specified http://en.wikipedia.org/wiki/IETF_language_tag[IETF language tag].
	 * 
	 * @param languageTag
	 * @return Found language or null if none could be found
	 */
	public Language findByLanguageTag(String languageTag) {
		return fg.v().has("languageTag", languageTag).has(Language.class).nextOrDefault(Language.class, null);
	}


	/**
	 * The tag language is currently fixed to english since we only want to store tags based on a single language. The idea is that tags will be localizable in
	 * the future.
	 */
	public Language getTagDefaultLanguage() {
		return findByLanguageTag("en");
	}

	public LanguageRoot findRoot() {
		return fg.v().has(LanguageRoot.class).nextOrDefault(LanguageRoot.class, null);
	}

}
