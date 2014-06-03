package com.google.glassware;
/**
 * Controls the behavior when the user picks the menu option. Allowed values are: - CUSTOM -
 * Custom action set by the service. When the user selects this menuItem, the API triggers a
 * notification to your callbackUrl with the userActions.type set to CUSTOM and the
 * userActions.payload set to the ID of this menu item. This is the default value.  - Built-in
 * actions:   <br/> REPLY - Initiate a reply to the timeline item using the voice recording UI. The
 * creator attribute must be set in the timeline item for this menu to be available.  <br/> REPLY_ALL
 * - Same behavior as REPLY. The original timeline item's recipients will be added to the reply
 * item.  <br/> DELETE - Delete the timeline item.  <br/> SHARE - Share the timeline item with the
 * available contacts.  <br/> READ_ALOUD - Read the timeline item's speakableText aloud; if this field
 * is not set, read the text field; if none of those fields are set, this menu item is ignored.
 * <br/> VOICE_CALL - Initiate a phone call using the timeline item's creator.phone_number attribute
 * as recipient.  <br/> NAVIGATE - Navigate to the timeline item's location.  <br/> TOGGLE_PINNED - Toggle
 * the isPinned state of the timeline item.  <br/> VIEW_WEBSITE - Open the payload of the menu item in
 * the browser.  <br/> PLAY_VIDEO - Open the payload of the menu item in the Glass video player.*/
enum BuiltinCardActions {
	DELETE("DELETE"), READ_ALOUD("READ_ALOUD"), SHARE("SHARE"), BUILT_IN_ACTION_REPLY(
			"REPLY");
	private final String actionString;

	BuiltinCardActions(String s) {
		this.actionString = s;
	}

	public String getActionString() {
		return this.actionString;
	}
}