package com.strategicgains.repoexpress.domain;

import java.util.UUID;

public class AbstractTimestampedUuidEntity
extends AbstractTimestampedEntity<UUID>
implements UuidEntity, Timestamped, TimestampedIdentifiable
{
}
