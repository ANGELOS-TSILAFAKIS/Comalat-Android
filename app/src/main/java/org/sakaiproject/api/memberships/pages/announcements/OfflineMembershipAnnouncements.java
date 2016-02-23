package org.sakaiproject.api.memberships.pages.announcements;

import android.content.Context;

import com.google.gson.Gson;

import org.sakaiproject.api.json.JsonParser;
import org.sakaiproject.api.pojos.announcements.Announcement;
import org.sakaiproject.api.user.User;
import org.sakaiproject.general.Actions;

import java.io.File;
import java.io.IOException;

/**
 * Created by vspallas on 09/02/16.
 */
public class OfflineMembershipAnnouncements {
    private Context context;
    private final Gson gson = new Gson();
    private String siteId;

    public OfflineMembershipAnnouncements(Context context, String siteId) {
        this.context = context;
        this.siteId = siteId;
    }

    public void getAnnouncements() {
        if (Actions.createDirIfNotExists(context, User.getUserEid() + File.separator + "memberships" + File.separator + siteId + File.separator + "announcements")) {
            try {
                String announce = Actions.readJsonFile(context, siteId + "_announcements", User.getUserEid() + File.separator + "memberships" + File.separator + siteId + File.separator + "announcements");
                Announcement announcement = gson.fromJson(announce, Announcement.class);
                JsonParser.getMembershipAnnouncements(announcement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}