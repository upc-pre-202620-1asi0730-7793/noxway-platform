package com.noctiva.noxway.iam.application.queryservices;

import com.noctiva.noxway.iam.application.queries.IamQueries.GetSecurityStatusQuery;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;

/**
 * Query contract exposing structured IAM module status.
 */
public interface IamQueryService {

  Result<SecurityStatusView, ApplicationError> handle(GetSecurityStatusQuery query);

  record SecurityStatusView(boolean securityEnabled, String mode, String message) {}
}
