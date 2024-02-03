/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ngapps.phototime.interests

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.ngapps.phototime.flingElementDownUp
import com.ngapps.phototime.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToInterestsScreen() {
    device.findObject(By.text("Interests")).click()
    device.waitForIdle()
    // Wait until interests are shown on screen
    waitForObjectOnTopAppBar(By.text("Interests"))

    // Wait until content is loaded by checking if interests are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
}

fun MacrobenchmarkScope.interestsScrollTopicsDownUp() {
    device.wait(Until.hasObject(By.res("interests:topics")), 5_000)
    val topicsList = device.findObject(By.res("interests:topics"))
    device.flingElementDownUp(topicsList)
}

fun MacrobenchmarkScope.interestsWaitForTopics() {
    device.wait(Until.hasObject(By.text("Accessibility")), 30_000)
}

fun MacrobenchmarkScope.interestsToggleBookmarked() {
    val topicsList = device.findObject(By.res("interests:topics"))
    val checkable = topicsList.findObject(By.checkable(true))
    checkable.click()
    device.waitForIdle()
}
