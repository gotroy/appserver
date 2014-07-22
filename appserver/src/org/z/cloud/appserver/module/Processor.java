package org.z.cloud.appserver.module;

import org.z.cloud.commom.module.Module;
import org.z.cloud.commom.module.ModuleFactory;
import org.z.cloud.commom.processor.Request;
import org.z.cloud.commom.processor.Response;
import org.z.cloud.common.util.ClassUtil;
import org.z.cloud.common.util.StringUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class Processor {

	public static Response execute(Request request) {
		if (request == null)
			return createNoRequestResponse();
		Object result = executeModule(request);
		if (result != null)
			return createSuccessResponse(result, request);
		return createErrorResponse(request);
	}

	private static Response createNoRequestResponse() {
		BasicDBObject message = new BasicDBObject("message", "request is null");
		return new Response(false, null, message.toString());
	}

	private static Object executeModule(Request request) {
		Module module = getModule(request);
		if (module == null)
			return null;
		if (isDefaultMethod(request))
			return module.service(request.getArguments());
		return ClassUtil.execute(module, request.getMethodName(), request.getArguments());
	}

	private static Module getModule(Request request) {
		return ModuleFactory.INSTANCES.getModule(request.getModuleName());
	}

	private static boolean isDefaultMethod(Request request) {
		String method = request.getMethodName();
		if (StringUtil.isEmpty(method) || "service".equals(method))
			return true;
		return false;
	}

	private static Response createErrorResponse(Request request) {
		return new Response(false, null, createErrorMessage(request));
	}

	private static String createErrorMessage(Request request) {
		return createResponseMessage(request, "fail");
	}

	private static Response createSuccessResponse(Object result, Request request) {
		return new Response(true, getResponseResult(request, result), createSuccessMessage(request));
	}

	private static Object getResponseResult(Request request, Object result) {
		if (result == getModule(request))
			return "void";
		else
			return result;
	}

	private static String createSuccessMessage(Request request) {
		return createResponseMessage(request, "success");
	}

	private static String createResponseMessage(Request request, String message) {
		BasicDBObject result = new BasicDBObject();
		result.append("message", message);
		result.append("module", request.getModuleName());
		result.append("method", request.getMethodName());
		result.append("arguments", createResponseArgs(request.getArguments()));
		return result.toString();
	}

	private static BasicDBList createResponseArgs(Object[] arguments) {
		if (arguments == null || arguments.length == 0)
			return null;
		BasicDBList result = new BasicDBList();
		for (Object argument : arguments)
			result.add(argument.toString());
		return result;
	}

}
