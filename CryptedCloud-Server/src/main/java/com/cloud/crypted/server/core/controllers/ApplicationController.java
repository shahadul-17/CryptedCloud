package com.cloud.crypted.server.core.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.cloud.crypted.server.Application;
import com.cloud.crypted.server.core.DynamicResources;
import com.cloud.crypted.server.core.utilities.FileUtilities;
import com.cloud.crypted.server.core.utilities.RequestIDProvider;
import com.cloud.crypted.server.core.utilities.StringUtilities;

@RestController
@RequestMapping(path="v{version}/{resourceName}")
public class ApplicationController {
	
	@GetMapping
	public Object get(@PathVariable(name="version") String version,
			@PathVariable(name="resourceName") String resourceName,
			@RequestParam(required=true) Map<String, String> requestParameters) {
		Map<String, String> response = new HashMap<String, String>(
				(int) DynamicResources.getConfiguration("collection.initialCapacity"));
		
		if (!StringUtilities.isNullOrEmpty((String) DynamicResources.getConfiguration("status"))) {
			response.put("errorMessage", DynamicResources.getErrorMessage("status.maintenance"));
			
			return response;
		}
		
		// we could've included the 'v' in path variable, 'version'...
		if (!FileUtilities.directoryExists(Application.DYNAMIC_RESOURCES_DIRECTORY_PATH + "v" + version)) {
			response.put("errorMessage", DynamicResources.getErrorMessage("unsupportedAPIVersion"));
			
			return response;
		}
		
		ModelAndView modelAndView = new ModelAndView("redirect:/" + resourceName + "/get");
		modelAndView.addAllObjects(requestParameters);
		modelAndView.addObject("requestID", RequestIDProvider.generateID());		// for security purpose...
		
		return modelAndView;
	}
	
	@PostMapping
	public Object post(@PathVariable(name="version") String version,
			@PathVariable(name="resourceName") String resourceName,
			@RequestBody(required=true) Map<String, Object> requestBody,
			HttpSession httpSession) {
		Map<String, String> response = new HashMap<String, String>((int) DynamicResources.getConfiguration("collection.initialCapacity"));
		
		if (!StringUtilities.isNullOrEmpty((String) DynamicResources.getConfiguration("status"))) {
			response.put("errorMessage", DynamicResources.getErrorMessage("status.maintenance"));
			
			return response;
		}
		
		// we could've included the 'v' in path variable, 'version'...
		// but this is our design choice...
		if (!FileUtilities.directoryExists(Application.DYNAMIC_RESOURCES_DIRECTORY_PATH + "v" + version)) {
			response.put("errorMessage", DynamicResources.getErrorMessage("unsupportedAPIVersion"));
			
			return response;
		}
		
		String requestID = RequestIDProvider.generateID();
		httpSession.setAttribute(requestID, requestBody);		// worst design choice we could possibly make :P :P
		
		ModelAndView modelAndView = new ModelAndView("redirect:/" + resourceName + "/post");
		modelAndView.addObject("requestID", requestID);		// for security purpose...
		
		return modelAndView;
	}
	
	@PutMapping
	public Object put(@PathVariable(name="version") String version,
			@PathVariable(name="resourceName") String resourceName,
			@RequestBody(required=true) Map<String, Object> requestBody,
			HttpSession httpSession) {
		Map<String, String> response = new HashMap<String, String>(
				(int) DynamicResources.getConfiguration("collection.initialCapacity"));
		
		if (!StringUtilities.isNullOrEmpty((String) DynamicResources.getConfiguration("status"))) {
			response.put("errorMessage", DynamicResources.getErrorMessage("status.maintenance"));
			
			return response;
		}
		
		// we could've included the 'v' in path variable, 'version'...
		// but this is our design choice...
		if (!FileUtilities.directoryExists(Application.DYNAMIC_RESOURCES_DIRECTORY_PATH + "v" + version)) {
			response.put("errorMessage", DynamicResources.getErrorMessage("unsupportedAPIVersion"));
			
			return response;
		}
		
		String requestID = RequestIDProvider.generateID();
		httpSession.setAttribute(requestID, requestBody);		// worst design choice we could possibly make :P :P
		
		ModelAndView modelAndView = new ModelAndView("redirect:/" + resourceName + "/put");
		modelAndView.addObject("requestID", requestID);		// for security purpose...
		
		return modelAndView;
	}
	
	@DeleteMapping
	public Object delete(@PathVariable(name="version") String version,
			@PathVariable(name="resourceName") String resourceName,
			@RequestParam(required=true) Map<String, String> requestParameters) {
		Map<String, String> response = new HashMap<String, String>(
				(int) DynamicResources.getConfiguration("collection.initialCapacity"));
		
		if (!StringUtilities.isNullOrEmpty((String) DynamicResources.getConfiguration("status"))) {
			response.put("errorMessage", DynamicResources.getErrorMessage("status.maintenance"));
			
			return response;
		}
		
		// we could've included the 'v' in path variable, 'version'...
		if (!FileUtilities.directoryExists(Application.DYNAMIC_RESOURCES_DIRECTORY_PATH + "v" + version)) {
			response.put("errorMessage", DynamicResources.getErrorMessage("unsupportedAPIVersion"));
			
			return response;
		}
		
		ModelAndView modelAndView = new ModelAndView("redirect:/" + resourceName + "/delete");
		modelAndView.addAllObjects(requestParameters);
		modelAndView.addObject("requestID", RequestIDProvider.generateID());		// for security purpose...
		
		return modelAndView;
	}
	
}