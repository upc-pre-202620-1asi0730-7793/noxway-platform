package com.noctiva.noxway.moderation.application.commandservices;

import com.noctiva.noxway.moderation.application.commands.ModerationCommands.*;
import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;

public interface ModerationCommandService {

  Result<CommunityReport, ApplicationError> handle(SubmitCommunityReportCommand cmd);

  Result<CommunityReport, ApplicationError> handle(DecideReportCommand cmd);
}
