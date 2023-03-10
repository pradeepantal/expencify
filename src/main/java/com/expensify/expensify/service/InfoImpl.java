package com.expensify.expensify.service;

import com.expensify.expensify.dto.InfoRequest;
import com.expensify.expensify.model.Info;
import com.expensify.expensify.repo.AccountingInfo;
import com.expensify.expensify.util.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class InfoImpl {

    private final Logger logger = LoggerFactory.getLogger(InfoImpl.class);
    @Value("${partnerUserID}")
    private String partnerUserID;

    @Value("${expensifyUrl}")
    private String url;

    @Value("${partnerUserSecret}")
    private String partnerUserSecret;

    private AccountingInfo accountingInfo;
    public String sendPostRequestToExpensify(InfoRequest infoRequest) throws JsonProcessingException {
        String requestPolicyCreate = "requestJobDescription={\"type\":\"create\",\"credentials\":{\"partnerUserID\":\""+partnerUserID+"\",\"partnerUserSecret\":\""+partnerUserSecret+"\"},\"inputSettings\":{\"type\":\"policy\",\"policyName\":\""+infoRequest.getPolicyName()+"\"}}";
        String response = callExpensifyApi(url, requestPolicyCreate);
        logger.info("Response from policy create ::{}",response);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        String policyId = jsonNode.get("policyID").asText();
        logger.info("PolicyID ::{}",policyId);

        MultiValueMap<String , String> map = new LinkedMultiValueMap<>();
        map.add("requestJobDescription", "{\"type\":\"create\",\"credentials\":{\"partnerUserID\":\""+partnerUserID+"\",\"partnerUserSecret\":\""+partnerUserSecret+"\"},\"inputSettings\":{\"type\":\"expenses\",\"employeeEmail\":\""+infoRequest.getOwnerEmail()+"\",\"transactionList\":[{\"created\":\"2016-01-01\",\"currency\":\"USD\",\"merchant\":\"Name of merchant 1\",\"amount\":1234}]}}");
        String responseFromExpenseCreate = callExpenseCreateApi(url, map);

        /*
        * Invoice for the merchant
         */
        logger.info("ResponseFromExpenseCreate :: {}",responseFromExpenseCreate);
        JsonNode node = objectMapper.readTree(responseFromExpenseCreate).get("transactionList").get(0);
        int amount = node.get("amount").asInt();

        /*
        *Amount of the merchant
         */
        logger.info("Amount invested ::{}",amount);

        Info info = Info.builder()
                .vendorName(infoRequest.getOwnerEmail())
                .amountPaid(amount)
                .invoices(responseFromExpenseCreate)
                .build();

        accountingInfo.save(info);
        logger.info("Info after set all values ::{}",info);
        return "Success";
    }

    public String callExpensifyApi(String url, String requestBody) {
      try {
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
          HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
          RestTemplate restTemplate = new RestTemplate();
          ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
          return response.getBody();
      }catch (Exception e){
          throw new CustomException("Exception Occur While creating policy");
      }
    }

    public String callExpenseCreateApi(String url, MultiValueMap<String, String> map ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        }catch (Exception e){
            throw new CustomException("Exception while createExpenses");
        }
    }
}
