/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.util.*;
import javax.servlet.http.*;

import net.pieroxy.ua.detection.*;

/**
 * If this object is in the session, it represents the state of whether to user has made any mobile requests.
 */
public enum MobileStatus {
    /**
     * Once the user goes to a mobile page, we keep sending him/her to mobile pages where available.
     */
    MOBILE_PAGES_WHERE_AVAILABLE,
    /**
     * If a user clicks the link to go to the non-mobile page, we send him/her to non-mobile pages
     */
    REGULAR_PAGES_ALWAYS,
    /**
     * If the user has not been to a mobile page yet, we don't know what the preference is.
     */
    HAVE_NOT_REQUESTED_MOBILE_PAGE_YET_IN_SESSION;
    // -----------------------------------------------------------
    public static final String MOBILE_SESSION_ATTRIBUTE = "mobile";

    private static WeakHashMap<String, UserAgentDetectionResult> cache = new WeakHashMap<>();

    public static MobileStatus getMobileRequest (HttpServletRequest request, String requestUri) {
        HttpSession session = request.getSession();
        String nonMobile = request.getParameter("nonMobile");
        MobileStatus mobileStatus;
        // if ask for mobile page, go back to mobile page view
        if ("true".equals(nonMobile)) {
            mobileStatus = REGULAR_PAGES_ALWAYS;
        } else if ("false".equals(nonMobile)) {
            mobileStatus = MOBILE_PAGES_WHERE_AVAILABLE;
        } else {
            // use existing value if present, otherwise initialize
            mobileStatus = (MobileStatus) session.getAttribute(MOBILE_SESSION_ATTRIBUTE);
            if (mobileStatus == null) {
                mobileStatus = isOnMobileDevice(request) 
                ? MOBILE_PAGES_WHERE_AVAILABLE: HAVE_NOT_REQUESTED_MOBILE_PAGE_YET_IN_SESSION;
            }
        }
        session.setAttribute(MOBILE_SESSION_ATTRIBUTE, mobileStatus);
        return mobileStatus;
    }

    private static boolean isOnMobileDevice (HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        UserAgentDetectionResult agent = cache.get(userAgent);
        if (agent == null) {
			agent = new UserAgentDetector().parseUserAgent(userAgent); 
			cache.put(userAgent, agent);
        }

		DeviceType type = agent.getDevice().getDeviceType();
		return (type == DeviceType.PHONE || type == DeviceType.TABLET || type == DeviceType.UNKNOWN_MOBILE);
    }

}
