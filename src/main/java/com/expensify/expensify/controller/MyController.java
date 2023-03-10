package com.expensify.expensify.controller;

import com.expensify.expensify.dto.InfoRequest;
import com.expensify.expensify.service.InfoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private InfoImpl info;
    @PostMapping("/getInfoFromAccountingSystem")
    public ResponseEntity<String> createPolicy(@RequestBody InfoRequest infoRequest) throws Exception {

        String response = info.sendPostRequestToExpensify(infoRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
