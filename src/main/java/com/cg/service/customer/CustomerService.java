package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.service.IGeneralService;

import java.util.List;

public interface CustomerService extends IGeneralService<Customer> {

    List<Customer> findAllByIdNot(Long id);

    Boolean existsByEmail(String email);

    void deposit(Customer customer, Deposit deposit);

    void doTransfer(Transfer transfer);
}
