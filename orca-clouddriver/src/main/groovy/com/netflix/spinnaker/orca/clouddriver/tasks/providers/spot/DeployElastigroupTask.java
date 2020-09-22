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
import com.netflix.spinnaker.orca.ExecutionStatus;
import com.netflix.spinnaker.orca.Task;
import com.netflix.spinnaker.orca.TaskResult;
import com.netflix.spinnaker.orca.clouddriver.KatoService;
import com.netflix.spinnaker.orca.clouddriver.model.TaskId;
import com.netflix.spinnaker.orca.clouddriver.tasks.AbstractCloudProviderAwareTask;
import com.netflix.spinnaker.orca.clouddriver.utils.HealthHelper;
import com.netflix.spinnaker.orca.pipeline.model.Stage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeployElastigroupTask extends AbstractCloudProviderAwareTask implements Task {

  public static final String TASK_NAME = "deployElastigroup";
  @Autowired KatoService katoService;

  @Nonnull
  @Override
  public TaskResult execute(@Nonnull Stage stage) {
    String cloudProvider = getCloudProvider(stage);

    Map<String, Object> task = new HashMap<>(stage.getContext());
    Map<String, Map> operation =
        new ImmutableMap.Builder<String, Map>().put(TASK_NAME, task).build();

    String credentials = getCredentials(stage);
    TaskId taskId =
        katoService
            .requestOperations(cloudProvider, Collections.singletonList(operation))
            .toBlocking()
            .first();

    Map<String, Object> outputs = new HashMap<>();
    outputs.put("notification.type", TASK_NAME);
    outputs.put("kato.result.expected", true);
    outputs.put("kato.last.task.id", taskId);
    outputs.put("deployElastigroup.account.name", credentials);

    if (stage.getContext().containsKey("suspendedProcesses")) {
      Set<String> suspendedProcesses = (Set<String>) stage.getContext().get("suspendedProcesses");
      if (suspendedProcesses != null && suspendedProcesses.contains("AddToLoadBalancer")) {
        outputs.put(
            "interestingHealthProviderNames",
            HealthHelper.getInterestingHealthProviderNames(
                stage, Collections.singletonList("Amazon")));
      }
    }

    return TaskResult.builder(ExecutionStatus.SUCCEEDED).context(outputs).build();
  }
}
