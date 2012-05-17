/*
 * Copyright 2012 Albert Örwall
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.evactor.process.analyse.window

import akka.actor.ActorLogging
import scala.collection.immutable.SortedMap

/**
 *
 */
trait LengthWindow extends Window {

  type S
  val noOfRequests: Int

  override protected[analyse] def getInactive(activities: SortedMap[Long, S]): Map[Long, S] = {

    val noOfInactive = activities.size - noOfRequests

    if(noOfInactive > 0)
      activities.take(noOfInactive)
    else
      Map()

  }
}
