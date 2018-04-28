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
package org.apache.fineract.cn.cheque.api.v1.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class IssuingCount {

  private String accountIdentifier;
  private Integer start;
  @NotNull
  @Min(1)
  private Integer amount;

  public IssuingCount() {
    super();
  }

  public String getAccountIdentifier() {
    return this.accountIdentifier;
  }

  public void setAccountIdentifier(final String accountIdentifier) {
    this.accountIdentifier = accountIdentifier;
  }

  public Integer getStart() {
    return this.start;
  }

  public void setStart(final Integer start) {
    this.start = start;
  }

  public Integer getAmount() {
    return this.amount;
  }

  public void setAmount(final Integer amount) {
    this.amount = amount;
  }
}
