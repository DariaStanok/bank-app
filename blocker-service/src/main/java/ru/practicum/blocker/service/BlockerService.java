package ru.practicum.blocker.service;

import ru.practicum.platform.contracts.blocker.BlockerCheckRequest;
import ru.practicum.platform.contracts.blocker.BlockerCheckResponse;

public interface BlockerService {
	BlockerCheckResponse check(BlockerCheckRequest request);

}
