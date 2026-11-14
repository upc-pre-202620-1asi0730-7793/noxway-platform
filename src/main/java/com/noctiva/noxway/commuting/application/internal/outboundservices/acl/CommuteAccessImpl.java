package com.noctiva.noxway.commuting.application.internal.outboundservices.acl;

import com.noctiva.noxway.commuting.domain.repositories.CommuteRepository;
import com.noctiva.noxway.commuting.interfaces.acl.CommuteAccess;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommuteAccessImpl implements CommuteAccess {

  private final CommuteRepository commuteRepository;

  public CommuteAccessImpl(CommuteRepository commuteRepository) {
    this.commuteRepository = commuteRepository;
  }

  @Override
  public Result<Void, ApplicationError> verifyCompletedOwn(UUID commuteId, UUID workerId) {
    var commuteOpt = commuteRepository.findById(commuteId);
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    if (!commute.getWorkerId().equals(workerId)) {
      return Result.failure(
          new ApplicationError.ForbiddenError("You can only rate your own commutes"));
    }
    if (!"ARRIVED".equals(commute.getStatus())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "commuting.not_completed", "Only completed commutes can be rated"));
    }
    return Result.success(null);
  }
}
