package com.noctiva.noxway.iam.interfaces.acl;

import com.noctiva.noxway.iam.domain.model.valueobjects.Actor;
import java.util.UUID;

/**
 * Anti-Corruption Layer facade exposing the active actor context to business bounded contexts.
 */
public interface IamContextFacade {

  Actor resolveCurrentActor();

  boolean isSecurityEnabled();

  UUID defaultWorkerId();
}
