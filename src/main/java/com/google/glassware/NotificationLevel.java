package com.google.glassware;

enum NotificationLevel {
	DEFAULT("DEFAULT"), NONE(null);

	private final String level;

	NotificationLevel(String s) {
		this.level = s;
	}

	public String getLevel() {
		return level;
	}
}