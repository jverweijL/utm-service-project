package com.liferay.demo.utm.service;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;

import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
						Cookie cookie = new Cookie(param, request.getParameter(param));
						response.addCookie(cookie);
						containsUTM = true;
					}
				}

				if (containsUTM) {
					StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
					String queryString = request.getQueryString() + "&" + PROCESSED_UTM + "=1";
					response.sendRedirect(requestURL.append('?').append(queryString).toString());
					//TODO possible bug need to add path??
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(UtmService.class);
}