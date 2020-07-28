/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker.traces

import com.android.server.wm.flicker.assertions.AssertionsChecker
import com.android.server.wm.flicker.assertions.TraceAssertion
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject

/**
 * Base truth subject.
 */
abstract class SubjectBase<Trace : TraceBase<Entry>, Entry: ITraceEntry> protected constructor(
    fm: FailureMetadata,
    subject: Trace
) : Subject<SubjectBase<Trace, Entry>, Trace>(fm, subject) {
    protected val assertionsChecker = AssertionsChecker<Entry>()
    protected var newAssertion = true

    protected fun addAssertion(name: String, assertion: TraceAssertion<Entry>) {
        if (newAssertion) {
            assertionsChecker.add(assertion, name)
        } else {
            assertionsChecker.append(assertion, name)
        }
    }

    /**
     * Run the assertions for all trace entries
     */
    fun forAllEntries() {
        assertionsChecker.checkChangingAssertions()
        test()
    }

    /**
     * Run the assertions for all trace entries within the specified time range
     */
    fun forRange(startTime: Long, endTime: Long) {
        assertionsChecker.filterByRange(startTime, endTime)
        test()
    }

    /**
     * Run the assertions only in the first trace entry
     */
    fun inTheBeginning() {
        if (actual().entries.isEmpty()) {
            fail("No entries found.")
        }
        assertionsChecker.checkFirstEntry()
        test()
    }

    /**
     * Run the assertions only in the last  trace entry
     */
    fun atTheEnd() {
        if (actual().entries.isEmpty()) {
            fail("No entries found.")
        }
        assertionsChecker.checkLastEntry()
        test()
    }

    /**
     * Run the assertions
     */
    private fun test() {
        val failures = assertionsChecker.test(actual().entries)
        if (failures.isNotEmpty()) {
            val failureTracePath = actual().source
            val failureLogs = failures.joinToString("\n") { it.toString() }
            var tracePath = ""
            if (failureTracePath.isPresent) {
                tracePath = """
                    
                    $traceName Trace can be found in: ${failureTracePath.get().toAbsolutePath()}
                    Checksum: ${actual().sourceChecksum}
                    
                    """.trimIndent()
            }
            fail(tracePath + failureLogs)
        }
    }

    abstract val traceName: String
}