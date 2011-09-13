/**
 * 
 */
package org.sakaiproject.dash.listener;

import java.util.Date;

import org.apache.log4j.Logger;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.dash.logic.DashboardLogic;
import org.sakaiproject.dash.logic.DashboardLogicImpl;
import org.sakaiproject.dash.logic.SakaiProxy;
import org.sakaiproject.dash.model.CalendarItem;
import org.sakaiproject.dash.model.Context;
import org.sakaiproject.dash.model.NewsItem;
import org.sakaiproject.dash.model.Realm;
import org.sakaiproject.dash.model.SourceType;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.site.api.SiteService;

/**
 * 
 * THIS MAY BE MOVED TO THE site-manage PROJECT IN SAKAI CORE ONCE THE INTERFACE IS MOVED TO KERNEL
 */
public class SiteMembershipNewEventProcessor implements EventProcessor {

	private static Logger logger = Logger.getLogger(SiteMembershipNewEventProcessor.class);
	
	protected DashboardLogic dashboardLogic;
	public void setDashboardLogic(DashboardLogic dashboardLogic) {
		this.dashboardLogic = dashboardLogic;
	}
	
	protected SakaiProxy sakaiProxy;
	public void setSakaiProxy(SakaiProxy proxy) {
		this.sakaiProxy = proxy;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.dash.listener.EventProcessor#getEventIdentifer()
	 */
	public String getEventIdentifer() {

		// please refer to SakaiProxy instead of SiteService here
		return SiteService.EVENT_USER_SITE_MEMBERSHIP_ADD;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.dash.listener.EventProcessor#processEvent(org.sakaiproject.event.api.Event)
	 */
	public void processEvent(Event event) {
		// here is the format of resource string. We need to parse it to get uid, role, provide, group id separately
		//uid=8f6f32b0-2a25-4661-9be8-6cda2b479e14;role=access;active=true;provided=false
		String uid = null;
		String role = null;
		String active = null;
		String provided = null;
		
		String resource = event.getResource();
		String parts[] = resource.split(";");
		if( parts != null) {
			for(String pair : parts) {
				String entries[] = resource.split("=");
				if (entries != null)
				{
					if ("uid".equals(entries[0]))
					{
						uid = entries[1];
					}
					else if ("role".equals(entries[0]))
					{
						role = entries[1];
					}
					else if ("active".equals(entries[0]))
					{
						active = entries[1];
					}
					else if ("provided".equals(entries[0]))
					{
						provided = entries[1];
					}
				}
			}
		}

		Context context = this.dashboardLogic.getContext(event.getContext());
		if(context == null) {
			context = this.dashboardLogic.createContext(event.getContext());
		}
		
		// Zhen:
		// if I understand correctly, we will have a user-id and a site-id with semantics 
		// indicating this user now belongs to this site. This method will then call a 
		// method in DashboardLogic to add links for this user to dashboard items related
		// to this site. The implementation of the new DashboardLogic method will iterate
		// through all CalendarItems and NewsItems related to that site, check permissions
		// for this user based on the accessPermission defined in the SourceType object 
		// related to the dashboard item, and add a link if the user has that permission. 
		// Is that right? 
		
		// see dashboardLogic.addCalendarLinks(String sakaiUserId, String contextId)
		// see dashboardLogic.addNewsLinks(String sakaiUserId, String contextId)
		
		
		/*SourceType sourceType = this.dashboardLogic.getSourceType("assignment");
		if(sourceType == null) {
			sourceType = this.dashboardLogic.createSourceType("assignment", SakaiProxy.PERMIT_ASSIGNMENT_ACCESS);
		}
		
		NewsItem newsItem = this.dashboardLogic.createNewsItem(assn.getTitle(), event.getEventTime(), assn.getReference(), assn.getUrl(), context, sourceType);
		this.dashboardLogic.createNewsLinks(newsItem);
	
		CalendarItem calendarItem = this.dashboardLogic.createCalendarItem(assn.getTitle(), new Date(assn.getDueTime().getTime()), assn.getReference(), assn.getUrl(), context, sourceType);
		this.dashboardLogic.createCalendarLinks(calendarItem);*/
	}

	public void init() {
		
		this.dashboardLogic.registerEventProcessor(this);
	}
}
