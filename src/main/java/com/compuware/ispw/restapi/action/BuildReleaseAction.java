/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2019 Compuware Corporation. All rights reserved.
 */
package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import com.compuware.ispw.model.rest.BuildResponse;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * 
 */
public class BuildReleaseAction extends SetInfoPostAction
{
	private static final String[] defaultProps = new String[]{releaseId, level, runtimeConfiguration};

	private static final String contextPath = "/ispw/{srid}/releases/{releaseId}/tasks/build?level={level}&assignmentId={assignmentId}"; //$NON-NLS-1$

	public static String getDefaultProps()
	{
		return RestApiUtils.join(Constants.LINE_SEPARATOR, defaultProps, true);
	}

	public BuildReleaseAction(PrintStream logger)
	{
		super(logger);
	}

	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{
		return getIspwRequestBean(srid, ispwRequestBody, webhookToken, contextPath);
	}

	@SuppressWarnings("nls")
	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Building tasks in release " + ispwContextPathBean.getReleaseId() + " at level "
				+ ispwContextPathBean.getLevel());
	}

	@SuppressWarnings("nls")
	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		BuildResponse buildResp = new JsonProcessor().parse(responseJson, BuildResponse.class);
		logger.println("Set " + buildResp.getSetId() + " created to build tasks in release "
				+ ispwRequestBean.getIspwContextPathBean().getReleaseId());

		return buildResp;
	}
}
