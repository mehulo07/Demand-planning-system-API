package com.v1.DemandPlanningService.service;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1.DemandPlanningService.bean.ProductInfo;
import com.v1.DemandPlanningService.bean.ProductParamMaster;
import com.v1.DemandPlanningService.bean.TransactionSeq;
import com.v1.DemandPlanningService.constant.DpsConstant;
import com.v1.DemandPlanningService.dao.ProductCategoryDaoImpl;
import com.v1.DemandPlanningService.dao.ProductParamDetailDaoImpl;
import com.v1.DemandPlanningService.dao.ProductParamMasterDaoImpl;
import com.v1.DemandPlanningService.dao.ProductSettingDao;
import com.v1.DemandPlanningService.dao.TemplateTypeDaoImpl;
import com.v1.DemandPlanningService.dao.TransactionSeqDaoImpl;
import com.v1.DemandPlanningService.dto.ProductSeattingSearch;
import com.v1.DemandPlanningService.dto.Response;
import com.v1.DemandPlanningService.tokenutility.DPSUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class ProductSettingService {

	private static final Logger logger = Logger.getLogger(ProductSettingService.class);
	
	@Autowired
	private ProductSettingDao prodSettingDao;
	
	@Autowired
	private TemplateTypeDaoImpl templateTypeDaoImpl;
	
	@Autowired
	private ProductCategoryDaoImpl productCategoryDaoImpl;
	
	@Autowired
	private ProductParamDetailDaoImpl productParamDetailDaoImpl;
	
	@Autowired
	private TransactionSeqDaoImpl transactionSeqDaoImpl;
	
	@Autowired
	private ProductParamMasterDaoImpl  productParamMasterDaoImpl;
	
	@Autowired
	private DPSUtility dPSUtility;
	
	public Response getProductSettingDetailsService(String userId) {
		logger.info("(getProductSettingDetailsService) Execution Start.");
		Response res = new Response();
		JSONObject obj = new JSONObject ();
		
		obj.put("ProdCategorys", productCategoryDaoImpl.getCategoryListByUserId(userId, DpsConstant.STATUS_ACTIVE));
		//obj.put("stockCategoryDetails", stockCategoryDaoImpl.getCategoryListByUserId(userId, DpsConstant.STATUS_ACTIVE));
		obj.put("TemplateTypes", templateTypeDaoImpl.getTemplateListByUserId(userId, DpsConstant.STATUS_ACTIVE));
		//obj.put("TransactionId", transactionSeqDaoImpl.getByName(DpsConstant..DPS_COMPANY_PREFIX,DpsConstant.STATUS_ACTIVE));
		res.setObject((Object)obj);
		logger.info("(getProductSettingDetailsService) Execution Ends.");
		
		return res;
	}
	
	
	public List<ProductInfo> getProductListService(ProductInfo productInfoBean , int limit){
		logger.info("(getProductListService) Execution:");
		return prodSettingDao.getProductListDao(productInfoBean, limit);
	}
	
	@Transactional
	public Response saveCategoryProductParam(ProductParamMaster productParamMasterBean , Response responseBean) {
		logger.info("(saveCategoryProductParam) Execution Start.");
		try {
			productParamMasterDaoImpl.delete(productParamMasterBean);
			TransactionSeq seq = transactionSeqDaoImpl.getByName("DPS", "");
			productParamMasterBean.setTransaction_id(seq.getTransaction_id());
			productParamMasterDaoImpl.save(productParamMasterBean);
			responseBean.setStatusCode(DpsConstant.SUCCESS_CODE);
			responseBean.setStatusMessage(DpsConstant.SUCCESS_MESSAGE);
			responseBean.setObject((Object)"Product Setting Inserted Successfully");
		}catch (Exception e) {
			responseBean.setStatusCode(DpsConstant.CLIENT_ERROR_CODE);
			responseBean.setStatusMessage(DpsConstant.ERROR_MESSAGE);
			responseBean.setObject((Object)"Error");
		}
		logger.info("(saveCategoryProductParam) Execution Ends.");
		return responseBean;
	}
	
	
	
	public void ProductCategoryParamList(ProductSeattingSearch productSeattingSearch , Response response) {
		logger.debug("(ProductCategoryParamList) Execution Ends.");
		Date dateFrom = null , dateTo = null;
		List<Integer> listOfMonth = null;
		int firstMonth, lastMonth;
		List<Date> dateList = new ArrayList<Date>();
		ProductParamMaster categoryProductParamMaster = null;
		List<ProductParamMaster> productParamMasterList = new ArrayList<>();
		JSONObject jsonObj = new JSONObject();
		List<TransactionSeq> transactions = null; 
		int year;
		
		JSONArray jsonArray = new JSONArray();
		try {
			year =productSeattingSearch.getYear();
			listOfMonth = productSeattingSearch.getMonth();
			
			firstMonth = (int)dPSUtility.getFirstMonth(listOfMonth);
			lastMonth = (int)dPSUtility.getLastMonth(listOfMonth);
			
			dateFrom = dPSUtility.getFirstDate( year, firstMonth);
			dateTo = dPSUtility.getLastDate( year, lastMonth);
			dateList.add(dateFrom);
			dateList.add(dateTo);
			
			if(productSeattingSearch.isSearch()) {
				//go for search
				
			}else {
				
				categoryProductParamMaster = productParamMasterDaoImpl.getByStatus(productSeattingSearch, dateFrom,dateTo);
				categoryProductParamMaster.setProductParamDetailBeanList(productParamDetailDaoImpl.getAllByID(categoryProductParamMaster.getTransaction_id()));
				
				//getAllProductDataRelatedToCategory
				transactions = transactionSeqDaoImpl.getAllByID(productSeattingSearch.getCategory());
				
				for (TransactionSeq tempTransactionSeq: transactions) {
					ProductParamMaster tempProductParamMaster = null;
					tempProductParamMaster = productParamMasterDaoImpl.getByID(tempTransactionSeq.getTransaction_id(), DpsConstant.STATUS_ACTIVE);
					if(tempProductParamMaster != null) {
						tempProductParamMaster.setProductParamDetailBeanList(productParamDetailDaoImpl.getAllByID(tempProductParamMaster.getTransaction_id()));
						productParamMasterList.add(tempProductParamMaster);
					}
				}
				//get all product based on category and product
				jsonObj.put("year",year);
				jsonObj.put("month",dPSUtility.getMonth(firstMonth));
				jsonObj.put("category",categoryProductParamMaster.getProductParamDetailBeanList().get(0).getStk_cat_name());
				jsonObj.put("categoryParam",categoryProductParamMaster);
				jsonObj.put("productParamList",productParamMasterList);
				
				response.setStatusCode(DpsConstant.SUCCESS_CODE);
				response.setStatusMessage(DpsConstant.SUCCESS_MESSAGE);
				response.setObject((Object)jsonObj);
			}
		}catch(Exception e){
			response.setStatusCode(DpsConstant.CLIENT_ERROR_CODE);
			response.setStatusMessage(DpsConstant.ERROR_MESSAGE);
			response.setObject((Object)DpsConstant.EXCEPTION_WHILE_DATA_FATCHING);
			System.out.println("Error inside service");
			e.printStackTrace();
		}
	}
	
}
