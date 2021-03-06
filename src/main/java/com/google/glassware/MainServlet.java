package com.google.glassware;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.common.collect.Lists;

/**
 * Handles POST requests from index.jsp
 * 
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());

	public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
	public static final String CONTACT_NAME = "Java Quick Start";

	public static final String PAGINATED_HTML = "<article class='auto-paginate'>"
			+ "<h2 class='blue text-large'>Did you know...?</h2>"
			+ "<p>Cats are <em class='yellow'>solar-powered.</em> The time they spend napping in "
			+ "direct sunlight is necessary to regenerate their internal batteries. Cats that do not "
			+ "receive sufficient charge may exhibit the following symptoms: lethargy, "
			+ "irritability, and disdainful glares. Cats will reactivate on their own automatically "
			+ "after a complete charge cycle; it is recommended that they be left undisturbed during "
			+ "this process to maximize your enjoyment of your cat.</p><br/><p>"
			+ "For more cat maintenance tips, tap to view the website!</p>" + "</article>";

	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String userId = AuthUtil.getUserId(req);
		Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
		String message = "";

		String operationParam = req.getParameter("operation");
		if (operationParam.equals("insertSubscription")) {

			// subscribe (only works deployed to production)
			try {
				MirrorClient.insertSubscription(credential, WebUtil.buildUrl(req, "/notify"), userId,
						req.getParameter("collection"));
				message = "Application is now subscribed to updates.";
			} catch (GoogleJsonResponseException e) {
				LOG.warning("Could not subscribe " + WebUtil.buildUrl(req, "/notify") + " because "
						+ e.getDetails().toPrettyString());
				message = "Failed to subscribe. Check your log for details";
			}

		} else if (operationParam.equals("deleteSubscription")) {

			// subscribe (only works deployed to production)
			MirrorClient.deleteSubscription(credential, req.getParameter("subscriptionId"));

			message = "Application has been unsubscribed.";

		} else if (operationParam.equals("insertItem")) {
			LOG.fine("Inserting Timeline Item");
			FeedItem.createTimeLineItem(req, credential);

			message = "A timeline item has been inserted.";

		} else if (operationParam.equals("insertPaginatedItem")) {
			LOG.fine("Inserting Timeline Item");
			FeedItem.createPaginatedItem(credential);

			message = "A timeline item has been inserted.";

		} else if (operationParam.equals("insertItemWithAction")) {
			LOG.fine("Inserting Timeline Item");
			FeedItem.createItemWithAction(req, credential);

			message = "A timeline item with actions has been inserted.";

		} else if (operationParam.equals("insertContact")) {
			if (req.getParameter("iconUrl") == null || req.getParameter("name") == null) {
				message = "Must specify iconUrl and name to insert contact";
			} else {
				// Insert a contact
				LOG.fine("Inserting contact Item");
				Contact contact = new Contact();
				contact.setId(req.getParameter("id"));
				contact.setDisplayName(req.getParameter("name"));
				contact.setImageUrls(Lists.newArrayList(req.getParameter("iconUrl")));
				contact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));
				MirrorClient.insertContact(credential, contact);

				message = "Inserted contact: " + req.getParameter("name");
			}

		} else if (operationParam.equals("deleteContact")) {

			// Insert a contact
			LOG.fine("Deleting contact Item");
			MirrorClient.deleteContact(credential, req.getParameter("id"));

			message = "Contact has been deleted.";

		} else if (operationParam.equals("insertItemAllUsers")) {
			if (req.getServerName().contains("glass-java-starter-demo.appspot.com")) {
				message = "This function is disabled on the demo instance.";
			}

			// Insert a contact
			List<String> users = AuthUtil.getAllUserIds();
			LOG.info("found " + users.size() + " users");
			if (users.size() > 10) {
				// We wouldn't want you to run out of quota on your first day!
				message = "Total user count is " + users.size() + ". Aborting broadcast " + "to save your quota.";
			} else {
				BatchCallback callback = insertAll(users);
				message = "Successfully sent cards to " + callback.success + " users (" + callback.failure
						+ " failed).";
			}

		} else if (operationParam.equals("deleteTimelineItem")) {

			// Delete a timeline item
			LOG.fine("Deleting Timeline Item");
			MirrorClient.deleteTimelineItem(credential, req.getParameter("itemId"));

			message = "Timeline Item has been deleted.";

		} else {
			String operation = operationParam;
			LOG.warning("Unknown operation specified " + operation);
			message = "I don't know how to do that";
		}
		WebUtil.setFlash(req, message);
		res.sendRedirect(WebUtil.buildUrl(req, "/"));
	}

	public static BatchCallback insertAll(List<String> users) throws IOException {
		TimelineItem allUsersItem = new TimelineItem();
		allUsersItem.setText("Hello Everyone!");

		BatchRequest batch = MirrorClient.getMirror(null).batch();
		BatchCallback callback = new BatchCallback();

		// TODO: add a picture of a cat
		for (String user : users) {
			Credential userCredential = AuthUtil.getCredential(user);
			MirrorClient.getMirror(userCredential).timeline().insert(allUsersItem).queue(batch, callback);
		}

		batch.execute();
		return callback;
	}
}
