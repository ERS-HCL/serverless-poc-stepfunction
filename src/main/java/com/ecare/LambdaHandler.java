/**
 * 
 */
package com.ecare;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ecare.constants.ActivityEnum;
import com.ecare.constants.Params;
import com.ecare.factory.ActivityFactory;
import com.ecare.model.response.PatientDetail;
import com.ecare.service.GetDataHandler;

/**
 * @author nilay.tiwari
 *
 */
public class LambdaHandler extends AbstractHandler<SpringConfig>
		implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private static Logger LOG = LoggerFactory.getLogger(LambdaHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		LOG.info(input + " Context data  " + context.toString());

		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap = (Map<String, Object>) input.get(Params.BODY_JSON);
		if (requestMap == null || requestMap.isEmpty()) {
			requestMap = input;
		}
		
		LOG.info("requestMap -- " + input + " -- context == " + context);
		String activity = (String) input.get("activity");
		if ("getdata".equals(activity)) {
			processGetRequest(input, responseMap);

		} else {
			processActionForWorkflow(requestMap, activity,responseMap);
		}
		
		return responseMap;
	}

	private void processActionForWorkflow(Map<String, Object> requestMap, String activity, Map<String, Object> responseMap) {
		ActivityEnum activityEnum = ActivityEnum.valueOf(activity);
		LOG.info("awsStepFunctionClient      ******  " + getApplicationContext().getBean("awsStepFunctionClient"));
		ActivityFactory activityFactory = (ActivityFactory) getApplicationContext().getBean("activityFactory");
		try {
			if (activityFactory.getInstance(activityEnum).isPresent()) {
				activityFactory.getInstance(activityEnum).get().performActivity(requestMap);
			} else {
				throw new Exception("Activity Operation not matched");
			}
			responseMap.put("message", "Done");
		} catch (Exception e) {
			if(Params.ALREADY_REGISTERED_EXCEPTION_MSG.equalsIgnoreCase(e.getMessage())) {
				responseMap.put("message", Params.ALREADY_REGISTERED_EXCEPTION_MSG);
			}else {
				throw new RuntimeException("Excetion while processing : " + e.getMessage(), e);
			}
		}
		
		
	}

	private void processGetRequest(Map<String, Object> input, Map<String, Object> responseMap) {
		@SuppressWarnings("unchecked")
		Map<String, Object> queryStringMap = (Map<String, Object>) ((Map<String, Object>) input.get("params"))
				.get("querystring");
		String patientId = (String) queryStringMap.get("patientId");
		GetDataHandler getDataHandler = (GetDataHandler) getApplicationContext().getBean("getDataHandler");
		PatientDetail ptDetail = getDataHandler.getData(patientId);
		if (ptDetail != null) {
			responseMap.put("data", ptDetail);
		} else {
			responseMap.put("message", "data not found");
		}
	}
}
