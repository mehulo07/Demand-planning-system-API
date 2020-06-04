package com.v1.DemandPlanningService.controller;





import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.v1.DemandPlanningService.constant.DpsConstant;
import com.v1.DemandPlanningService.dto.Response;
import com.v1.DemandPlanningService.dto.UserCredential;
import com.v1.DemandPlanningService.service.UserService;

import net.sf.json.JSONObject;

@RestController
@RequestMapping({"/user/v1/DPS"})
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class);
    
	@Autowired
	private UserService userService;
	
	@PostMapping({"/login"})
	@ResponseBody
	public Response userLogin(@Valid @RequestBody UserCredential requestBean , BindingResult result) {
		logger.info("Authentication of user :"+requestBean.getUsername());
		
		Response response =new Response();
		JSONObject jsonObject = new JSONObject();
		
		
		if(!result.hasErrors()) {
			userService.loginService(requestBean , response , jsonObject);
		}else {
			response.setStatusCode(DpsConstant.CLIENT_ERROR_CODE);
			response.setStatusMessage(DpsConstant.ERROR_MESSAGE);
			response.setObject((Object)DpsConstant.CLIENT_AUTHENTICATION_ERROR_MESSAGE_NULL);
		}
		return response;
	}
	
}
