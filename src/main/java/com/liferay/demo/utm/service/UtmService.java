package com.liferay.demo.utm.service;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;

import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author jverweij
 */

@Component(
		immediate = true,
		property = {"key=servlet.service.events.pre"},
		service = LifecycleAction.class
)
public class UtmService implements LifecycleAction {

	private static String PROCESSED_UTM = "pUTM";

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
			throws ActionException {

		if (_log.isDebugEnabled()) {
			_log.debug(String.format("layout.configuration.action.update=%s",lifecycleEvent ));
		}

		HttpServletResponse response = lifecycleEvent.getResponse();
		HttpServletRequest request = lifecycleEvent.getRequest();

		try {
			boolean containsUTM = false;
			boolean processedUTM = false;

			if (request.getParameter(PROCESSED_UTM) != null)
			{
				processedUTM = true;
			}

			if (!processedUTM) {
				for (String param : Collections.list(request.getParameterNames())) {
					if (param.toLowerCase().startsWith("utm_")) {
						//TODO possible bug with same utm_ params??
						System.out.println(param + ":" + request.getParameter(param));
						Cookie cookie = new Cookie(param, request.getParameter(param));
						response.addCookie(cookie);
						containsUTM = true;
					}
				}

				if (containsUTM) {
					String currentUrl = PortalUtil.getCurrentCompleteURL(request);
					response.sendRedirect(currentUrl  + "&" + PROCESSED_UTM + "=1");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(UtmService.class);
}