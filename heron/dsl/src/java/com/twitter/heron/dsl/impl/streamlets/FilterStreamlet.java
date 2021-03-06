//  Copyright 2017 Twitter. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.twitter.heron.dsl.impl.streamlets;

import java.util.Set;

import com.twitter.heron.api.topology.TopologyBuilder;
import com.twitter.heron.dsl.SerializablePredicate;
import com.twitter.heron.dsl.impl.BaseStreamlet;
import com.twitter.heron.dsl.impl.operators.FilterOperator;

/**
 * FilterStreamlet represents a Streamlet that is made up of elements from
 * the parent Streamlet after applying a user supplied filter function.
 */
public class FilterStreamlet<R> extends BaseStreamlet<R> {
  private BaseStreamlet<R> parent;
  private SerializablePredicate<? super R> filterFn;

  public FilterStreamlet(BaseStreamlet<R> parent, SerializablePredicate<? super R> filterFn) {
    this.parent = parent;
    this.filterFn = filterFn;
    setNumPartitions(parent.getNumPartitions());
  }

  @Override
  public boolean doBuild(TopologyBuilder bldr, Set<String> stageNames) {
    if (getName() == null) {
      setName(defaultNameCalculator("filter", stageNames));
    }
    if (stageNames.contains(getName())) {
      throw new RuntimeException("Duplicate Names");
    }
    stageNames.add(getName());
    bldr.setBolt(getName(), new FilterOperator<R>(filterFn),
        getNumPartitions()).shuffleGrouping(parent.getName());
    return true;
  }
}
