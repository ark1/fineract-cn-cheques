/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.cheque.service.internal.service;

import org.apache.fineract.cn.cheque.api.v1.domain.MICR;
import org.apache.fineract.cn.cheque.api.v1.domain.MICRResolution;
import org.apache.fineract.cn.cheque.service.ServiceConstants;
import org.apache.fineract.cn.cheque.service.internal.repository.IssuedChequeEntity;
import org.apache.fineract.cn.cheque.service.internal.repository.IssuedChequeRepository;
import org.apache.fineract.cn.cheque.service.internal.service.helper.CustomerService;
import org.apache.fineract.cn.cheque.service.internal.service.helper.DepositService;
import org.apache.fineract.cn.cheque.service.internal.service.helper.OrganizationService;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.deposit.api.v1.instance.domain.ProductInstance;
import org.apache.fineract.cn.lang.ServiceException;
import org.apache.fineract.cn.office.api.v1.domain.Office;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MICRService {

  private final Logger logger;
  private final ChequeService chequeService;
  private final OrganizationService organizationService;
  private final DepositService depositService;
  private final CustomerService customerService;
  private final IssuedChequeRepository issuedChequeRepository;

  @Autowired
  public MICRService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                     final ChequeService chequeService,
                     final OrganizationService organizationService,
                     final DepositService depositService,
                     final CustomerService customerService,
                     final IssuedChequeRepository issuedChequeRepository) {
    super();
    this.logger = logger;
    this.chequeService = chequeService;
    this.organizationService = organizationService;
    this.depositService = depositService;
    this.customerService = customerService;
    this.issuedChequeRepository = issuedChequeRepository;
  }

  public MICRResolution expand(final MICR micr) {

    if (this.chequeService.findBy(micr).isPresent()) {
      throw ServiceException.conflict("Cheque already used.");
    }

    final Office office =
        this.organizationService.findOffice(micr.getBranchSortCode())
            .orElseThrow(() -> ServiceException.notFound("Given MICR is unknown."));

    final IssuedChequeEntity issuedChequeEntity =
        this.issuedChequeRepository.findByAccountIdentifier(micr.getAccountNumber())
            .orElseThrow(() -> ServiceException.conflict("Cheque was never issued."));
    if (Integer.valueOf(micr.getChequeNumber()) > issuedChequeEntity.getLastIssuedNumber()) {
      throw ServiceException.conflict("Cheque number invalid.");
    }

    final ProductInstance productInstance =
        this.depositService.findProductInstance(micr.getAccountNumber())
            .orElseThrow(() -> ServiceException.badRequest("Given account not valid."));

    final Customer customer =
        this.customerService.findCustomer(productInstance.getCustomerIdentifier())
            .orElseThrow(() -> ServiceException.badRequest("Given customer not valid."));

    final MICRResolution micrResolution = new MICRResolution();
    micrResolution.setOffice(office.getName());
    micrResolution.setCustomer(customer.getGivenName() + " " + customer.getSurname());
    return micrResolution;
  }
}
