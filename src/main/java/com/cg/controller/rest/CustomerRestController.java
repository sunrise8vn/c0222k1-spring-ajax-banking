package com.cg.controller.rest;


import com.cg.exception.EmailExistsException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.DepositDTO;
import com.cg.model.dto.LocationRegionDTO;
import com.cg.service.customer.CustomerService;
import com.cg.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    @Autowired
    private AppUtil appUtil;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable long id) {
        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid customer ID");
        }

        return new ResponseEntity<>(customerOptional.get().toCustomerDTO(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> doCreate(@RequestBody CustomerDTO customerDTO) {

        customerDTO.setId(0L);
        customerDTO.setBalance(new BigDecimal(0L));
        customerDTO.getLocationRegion().setId(0L);

        Boolean exitsEmail = customerService.existsByEmail(customerDTO.getEmail());

        if (exitsEmail) {
            throw new EmailExistsException("Email already exists");
        }

        Customer newCustomer = customerService.save(customerDTO.toCustomer());

        return new ResponseEntity<>(newCustomer.toCustomerDTO(), HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> doDeposit(@Validated @RequestBody DepositDTO depositDTO, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) {
            return appUtil.mapErrorToResponse(bindingResult);
        }

        Optional<Customer> customerOptional = customerService.findById(Long.parseLong(depositDTO.getCustomerId()));

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid customer ID");
        }

        Customer customer = customerOptional.get();
        Deposit deposit = depositDTO.toDeposit(customer);

        customerService.deposit(customer, deposit);

        return new ResponseEntity<>("Deposit success", HttpStatus.CREATED);
    }
}
