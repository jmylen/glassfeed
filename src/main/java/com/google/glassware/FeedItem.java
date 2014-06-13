package com.google.glassware;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.common.collect.Lists;

public class FeedItem {

	private static final String DRILL_IN = "Drill In";

	public static TimelineItem createSimpleTextTimeLineItem(String text) {
		TimelineItem timelineItem = initDefaultTimelineItem();
		timelineItem.setText(text);
		return timelineItem;
	}

	public static TimelineItem initDefaultTimelineItem() {
		TimelineItem timelineItem = initDefaultTimelineItem();
		ArrayList<MenuItem> menuItemList = Lists.newArrayList();
		timelineItem.setMenuItems(menuItemList);
		timelineItem.setNotification(new NotificationConfig().setLevel(NotificationLevel.DEFAULT.getLevel()));
		return timelineItem;
	}

	public static TimelineItem createSimpleHtmlTimeLineItem(String html) {
		TimelineItem timelineItem = initDefaultTimelineItem();
		timelineItem.setHtml(html);
		return timelineItem;
	}

	public static void createPaginatedItem(Credential credential) throws IOException {
		TimelineItem timelineItem = createSimpleHtmlTimeLineItem(MainServlet.PAGINATED_HTML);

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		addUriPayload(timelineItem, "https://www.google.com/search?q=cat+maintenance+tips");
		timelineItem.setMenuItems(menuItemList);

		MirrorClient.insertTimelineItem(credential, timelineItem);
	}

	public static void addUriPayload(TimelineItem timelineItem, String uri) {
		List<MenuItem> menuItemList = timelineItem.getMenuItems();
		menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(uri));
	}

	public static void createItemWithAction(HttpServletRequest req, Credential credential) throws IOException {
		TimelineItem timelineItem = createItemWithCustomActions(req, "Tell me what you had for lunch :)");

		MirrorClient.insertTimelineItem(credential, timelineItem);
	}

	public static TimelineItem createItemWithCustomActions(HttpServletRequest req, String message) {
		TimelineItem timelineItem = createSimpleTextTimeLineItem(message);

		// Built in actions
		addMenuItems(timelineItem, BuiltinCardActions.values());

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		// And custom actions
		List<MenuValue> menuValues = new ArrayList<MenuValue>();
		menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/drill.png")).setDisplayName(
				DRILL_IN));
		menuItemList.add(new MenuItem().setValues(menuValues).setId("drill").setAction("CUSTOM"));

		timelineItem.setMenuItems(menuItemList);
		timelineItem.setNotification(new NotificationConfig().setLevel(NotificationLevel.DEFAULT.getLevel()));
		return timelineItem;
	}

	public static void addMenuItems(TimelineItem timelineItem, BuiltinCardActions[] actionList) {
		for (BuiltinCardActions action : actionList) {
			timelineItem.getMenuItems().add(new MenuItem().setAction(action.getActionString()));
		}
	}

	public static void createTimeLineItem(HttpServletRequest req, Credential credential) throws MalformedURLException,
			IOException {
		TimelineItem timelineItem = initDefaultTimelineItem();

		if (req.getParameter("message") != null) {
			timelineItem.setText(req.getParameter("message"));
		}

		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel(NotificationLevel.DEFAULT.getLevel()));

		if (req.getParameter("imageUrl") != null) {
			// Attach an image, if we have one
			URL url = new URL(req.getParameter("imageUrl"));
			String contentType = req.getParameter("contentType");
			MirrorClient.insertTimelineItem(credential, timelineItem, contentType, url.openStream());
		} else {
			MirrorClient.insertTimelineItem(credential, timelineItem);
		}
	}

}
