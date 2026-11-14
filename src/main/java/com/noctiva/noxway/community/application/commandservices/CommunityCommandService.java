package com.noctiva.noxway.community.application.commandservices;

import com.noctiva.noxway.community.application.commands.CommunityCommands.*;
import com.noctiva.noxway.community.application.queryservices.CommunityQueryService.ServicePointView;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;

public interface CommunityCommandService {

  Result<ServicePointView, ApplicationError> handle(SubmitServicePointCommand cmd);

  Result<ServicePointView, ApplicationError> handle(UpdateServicePointCommand cmd);

  Result<ServiceVote, ApplicationError> handle(VoteServicePointCommand cmd);

  Result<RiskZone, ApplicationError> handle(ReportRiskZoneCommand cmd);

  Result<RiskZone, ApplicationError> handle(ResolveRiskZoneCommand cmd);

  Result<RouteRating, ApplicationError> handle(RateRouteCommand cmd);

  Result<CommunityPost, ApplicationError> handle(CreateCommunityPostCommand cmd);

  Result<Void, ApplicationError> handle(DeleteCommunityPostCommand cmd);
}
