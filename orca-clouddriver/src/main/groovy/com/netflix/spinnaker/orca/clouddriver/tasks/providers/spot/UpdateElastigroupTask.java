/*
 * Copyright (c) 2019 Schibsted Media Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.orca.clouddriver.tasks.providers.spot;

import com.google.common.collect.ImmutableMap;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.netflix.spinnaker.orca.clouddriver.KatoService;
import com.netflix.spinnaker.orca.clouddriver.model.TaskId;
import com.netflix.spinnaker.orca.clouddriver.tasks.AbstractCloudProviderAwareTask;
import java.util.*;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateElastigroupTask extends AbstractCloudProviderAwareTask implements Task {

  public static final String TASK_NAME = "updateElastigroup";
  @Autowired KatoService katoService;

  @Nonnull
  @Override
  public TaskResult execute(@Nonnull StageExecution stage) {
    String cloudProvider = getCloudProvider(stage);

    Map<String, Object> task = new HashMap<>(stage.getContext());
    Map<String, Map> operation =
        new ImmutableMap.Builder<String, Map>().put(TASK_NAME, task).build();

    String credentials = getCredentials(stage);
    TaskId taskId =
        katoService.requestOperations(cloudProvider, Collections.singletonList(operation));

    Map<String, Object> outputs = new HashMap<>();

    outputs.put("deploy.account.name", credentials);
    outputs.put("kato.last.task.id", taskId);
    Map<String, Set<String>> serverGroupsByRegion = new HashMap<>();
    serverGroupsByRegion.put(
        task.get("region").toString(),
        new HashSet<>(Collections.singleton(task.get("serverGroupName").toString())));
    outputs.put("deploy.server.groups", serverGroupsByRegion);

    return TaskResult.builder(ExecutionStatus.SUCCEEDED).context(outputs).build();
  }
}
