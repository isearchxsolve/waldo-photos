package com.waldo;

import java.io.Serializable;
import java.util.List;

public class EXIFData implements Serializable {
	private static final long serialVersionUID = 5485705789182987317L;
	private final String key;
	private final List<EXIFDirectory> directories;

	public EXIFData(String key, List<EXIFDirectory> directories) {
		super();
		this.key = key;
		this.directories = directories;
	}

	public void addDirectory(EXIFDirectory directory) {
		directories.add(directory);
	}

	public String getKey() {
		return key;
	}

	public List<EXIFDirectory> getDirectories() {
		return directories;
	}

	public static class EXIFTag implements Serializable{
		private static final long serialVersionUID = -8816265326154041793L;
		private final Integer type;
		private final String name;
		private final String description;

		public EXIFTag(Integer type, String name, String description) {
			super();
			this.type = type;
			this.name = name;
			this.description = description;
		}

		public Integer getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

	}

	public static class EXIFDirectory implements Serializable{
		private static final long serialVersionUID = -1796601019024952518L;
		private final String name;
		private final List<EXIFTag> tags;

		public EXIFDirectory(String name, List<EXIFTag> tags) {
			super();
			this.name = name;
			this.tags = tags;
		}

		public void addTag(EXIFTag tag) {
			tags.add(tag);
		}

		public String getName() {
			return name;
		}

		public List<EXIFTag> getTags() {
			return tags;
		}

	}
}
