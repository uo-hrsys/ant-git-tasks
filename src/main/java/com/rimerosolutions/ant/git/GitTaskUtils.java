/*
 * Copyright 2013 Rimero Solutions
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
package com.rimerosolutions.ant.git;

import java.util.Collection;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.transport.TrackingRefUpdate;

/**
 * Utility class.
 *
 * @author Yves Zoundi
 */
public final class GitTaskUtils {

        /** Branding prefix to pre-prend to commit messages */
        public static final String BRANDING_MESSAGE = "[ant-git-tasks]";

        /** Branches name prefix */
        public static final String REF_HEAD_PREFIX = "refs/heads/";

        /** Tags name prefix */
        public static final String REF_TAG_PREFIX = "refs/tags/";

        /**
         * Checks whether or not a string is null or empty (as in blank)
         *
         * @param s The string to check
         * @return whether or not a string is null or empty (as in blank)
         */
        public static boolean isNullOrBlankString(String s) {
                if (s == null) {
                        return true;
                }

                return s.trim().length() == 0;
        }

        /**
         * Strips the 'ref/someprefix/' prefix from a reference name if needed
         *
         * @param refName The reference name to sanitize
         * @return The natural branch name without prefix
         */
        public static String sanitizeRefName(String refName) {
                if (refName.startsWith(REF_HEAD_PREFIX)) {
                        refName = refName.substring(REF_HEAD_PREFIX.length());
                }

                if (refName.startsWith(REF_TAG_PREFIX)) {
                        refName = refName.substring(REF_TAG_PREFIX.length());
                }

                return refName;
        }

        /**
         * Validate <code>if</code> and <code>else</code> conditions on a Git task
         *
         * @param t The git task to check
         */
        public static void validateTaskConditions(GitTask t) {
                if (t.getIf() != null || t.getUnless() != null) {
                        if (!(t.getIf() == null ^ t.getUnless() == null)) {
                                throw new GitBuildException("Either if or unless should be specified for a git task.");
                        }
                }
        }

        /**
         * Check references updates for any errors
         *
         * @param errorPrefix The error prefix for any error message
         * @param refUpdates A collection of tracking references updates
         */
        public static void validateTrackingRefUpdates(String errorPrefix, Collection<TrackingRefUpdate> refUpdates) {
                for (TrackingRefUpdate refUpdate : refUpdates) {
                        RefUpdate.Result result = refUpdate.getResult();

                        if (result == RefUpdate.Result.IO_FAILURE ||
                            result == RefUpdate.Result.LOCK_FAILURE ||
                            result == RefUpdate.Result.REJECTED ||
                            result == RefUpdate.Result.REJECTED_CURRENT_BRANCH ) {
                                throw new BuildException(String.format("%s - Status '%s'", errorPrefix, result.name()));
                        }
                }
        }

        private GitTaskUtils() {
                throw new AssertionError();
        }
}
