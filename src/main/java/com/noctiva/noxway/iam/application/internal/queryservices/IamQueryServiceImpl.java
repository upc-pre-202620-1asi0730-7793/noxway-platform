package com.noctiva.noxway.iam.application.internal.queryservices;

import com.noctiva.noxway.iam.application.queries.IamQueries.GetSecurityStatusQuery;
import com.noctiva.noxway.iam.application.queryservices.IamQueryService;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Query handler reporting whether IAM enforcement is enabled or structured in deferred mode.
 */
@Service
@Transactional(readOnly = true)
public class IamQueryServiceImpl implements IamQueryService {

  private final boolean securityEnabled;

  public IamQueryServiceImpl(@Value("${app.security.enabled:false}") boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  @Override
  public Result<SecurityStatusView, ApplicationError> handle(GetSecurityStatusQuery query) {
    String mode = securityEnabled ? "ENFORCED" : "STRUCTURED_PENDING_IMPLEMENTATION";
    String message =
        securityEnabled
            ? "IAM enforcement flag is active"
            : "IAM is structured cleanly and deferred; API operates with open local actor resolution";
    return Result.success(new SecurityStatusView(securityEnabled, mode, message));
  }
}
