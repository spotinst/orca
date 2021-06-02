/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.clouddriver.tasks.providers.spot

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spectator.api.Id
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.orca.api.pipeline.RetryableTask
import com.netflix.spinnaker.orca.api.pipeline.TaskResult
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import com.netflix.spinnaker.orca.clouddriver.CloudDriverCacheService
import com.netflix.spinnaker.orca.clouddriver.CloudDriverCacheStatusService
import com.netflix.spinnaker.orca.clouddriver.tasks.AbstractCloudProviderAwareTask
import com.netflix.spinnaker.orca.clouddriver.tasks.servergroup.ServerGroupCacheForceRefreshTask
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Clock
import java.util.concurrent.TimeUnit

import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus.RUNNING
import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus.SUCCEEDED

@Component
@Slf4j
class SpotServerGroupCacheForceRefreshTask extends ServerGroupCacheForceRefreshTask {

  long backoffPeriod = TimeUnit.SECONDS.toMillis(25)
  long timeout = TimeUnit.MINUTES.toMillis(35)

  SpotServerGroupCacheForceRefreshTask(CloudDriverCacheStatusService cacheStatusService, CloudDriverCacheService cacheService, ObjectMapper objectMapper, Registry registry) {
    super(cacheStatusService, cacheService, objectMapper, registry)
  }
}
