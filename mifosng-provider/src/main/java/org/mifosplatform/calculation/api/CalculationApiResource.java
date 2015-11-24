/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.calculation.api;

import java.util.HashSet;
import java.util.Set;



import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Path("/calculate")
@Component
@Scope("singleton")
public class CalculationApiResource {
	
	
	private final Set<String> CALCULATION_DATA_PARAMETERS = new HashSet<>(java.util.Arrays.asList("principal","interest","numberOfRepayments","expectedFirstRepaymentOnDate"));
	
	private final PlatformSecurityContext context;
	private final LoanScheduleCalculationPlatformService calculationPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer;
	
	@Autowired
	public CalculationApiResource(final PlatformSecurityContext context,final LoanScheduleCalculationPlatformService calculationPlatformService,final FromJsonHelper fromJsonHelper,
			final ApiRequestParameterHelper apiRequestParameterHelper, final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer){
		this.context = context;
		this.calculationPlatformService = calculationPlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.loanScheduleToApiJsonSerializer = loanScheduleToApiJsonSerializer;
	}
	
	@POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String calculateLoanSchedule(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson){
		
		final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
		final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery,this.fromJsonHelper);
		
		final LoanScheduleModel loanSchedule = this.calculationPlatformService.calculateLoanSchedule(query, true);
		
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		
		return this.loanScheduleToApiJsonSerializer.serialize(settings,loanSchedule.toData(),new HashSet<String>());
		
		
	}
	
	

	

}
