/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.sts.common;

import static org.junit.Assert.assertEquals;

import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.device.ITestDevice;
import com.android.tradefed.util.CommandResult;
import com.android.tradefed.util.CommandStatus;

public final class CommandUtil {

    private CommandUtil() {}

    /**
     * Execute shell command on device, throws AssertionError upon failure.
     *
     * @param device the device to use
     * @param cmd the command to run
     * @return the result of device.executeShellV2Command
     */
    public static CommandResult runAndCheck(ITestDevice device, String cmd)
            throws DeviceNotAvailableException {
        CommandResult res = device.executeShellV2Command(cmd);
        String failMsg =
                String.format(
                        "cmd failed: %s\ncode: %s\nstdout:\n%s\nstderr:\n%s",
                        cmd, res.getExitCode(), res.getStdout(), res.getStderr());
        assertEquals(failMsg, CommandStatus.SUCCESS, res.getStatus());
        return res;
    }
}
