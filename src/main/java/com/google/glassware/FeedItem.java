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

	public static TimelineItem createStaticTimeLineItem(String text) {
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setText(text);
		timelineItem.setNotification(new NotificationConfig()
				.setLevel(NotificationLevel.DEFAULT.getLevel()));
		List<MenuItem> menuItems = Lists.newArrayList();
		MenuItem item = new MenuItem();
		item.setAction(BuiltinCardActions.DELETE.getActionString());
		item.setAction(BuiltinCardActions.READ_ALOUD.getActionString());
		item.setAction(BuiltinCardActions.SHARE.getActionString());
		menuItems.add(item);
		timelineItem.setMenuItems(menuItems);
		return timelineItem;
	}

	public static void createPaginatedItem(Credential credential)
			throws IOException {
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setHtml(MainServlet.PAGINATED_HTML);

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(
				"https://www.google.com/search?q=cat+maintenance+tips"));
		timelineItem.setMenuItems(menuItemList);

		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig()
				.setLevel(NotificationLevel.DEFAULT.getLevel()));

		MirrorClient.insertTimelineItem(credential, timelineItem);
	}

	public static void createItemWithAction(HttpServletRequest req,
			Credential credential) throws IOException {
		TimelineItem timelineItem = createFeedItem(req,
				"Tell me what you had for lunch :)");

		MirrorClient.insertTimelineItem(credential, timelineItem);
	}

	public static TimelineItem createFeedItem(HttpServletRequest req,
			String message) {
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setText(message);

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		// Built in actions
		addMenuItems(menuItemList, BuiltinCardActions.values());

		// And custom actions
		List<MenuValue> menuValues = new ArrayList<MenuValue>();
		menuValues.add(new MenuValue().setIconUrl(
				WebUtil.buildUrl(req, "/static/images/drill.png"))
				.setDisplayName("Drill In"));
		menuItemList.add(new MenuItem().setValues(menuValues).setId("drill")
				.setAction("CUSTOM"));

		timelineItem.setMenuItems(menuItemList);
		timelineItem.setNotification(new NotificationConfig()
				.setLevel(NotificationLevel.DEFAULT.getLevel()));
		return timelineItem;
	}

	public static void addMenuItems(List<MenuItem> menuItemList,
			BuiltinCardActions[] actionList) {
		for (BuiltinCardActions action : actionList) {
			menuItemList
					.add(new MenuItem().setAction(action.getActionString()));
		}

	}

	public static void createTimeLineItem(HttpServletRequest req,
			Credential credential) throws MalformedURLException, IOException {
		TimelineItem timelineItem = new TimelineItem();

		if (req.getParameter("message") != null) {
			timelineItem.setText(req.getParameter("message"));
		}

		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig()
				.setLevel(NotificationLevel.DEFAULT.getLevel()));

		if (req.getParameter("imageUrl") != null) {
			// Attach an image, if we have one
			URL url = new URL(req.getParameter("imageUrl"));
			String contentType = req.getParameter("contentType");
			MirrorClient.insertTimelineItem(credential, timelineItem,
					contentType, url.openStream());
		} else {
			MirrorClient.insertTimelineItem(credential, timelineItem);
		}
	}

}
