package com.noctiva.noxway.commuting.interfaces.acl;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import java.util.UUID;

public interface CommuteAccess {

  Result<Void, ApplicationError> verifyCompletedOwn(UUID commuteId, UUID workerId);
}
