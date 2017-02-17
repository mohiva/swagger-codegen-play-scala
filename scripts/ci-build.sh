#!/bin/bash -e
#
# Builds the project in the continuous integration environment.
#
# Copyright 2015 Mohiva Organisation (license at mohiva dot com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -o nounset -o errexit

SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo ""
echo "Testing and generating documentation"
${SCRIPTS_DIR}/sbt.sh clean coverage test coverageReport

echo ""
echo "Aggregate coverage from sub-projects"
${SCRIPTS_DIR}/sbt.sh coverageAggregate

echo ""
echo "Build finished"
echo ""
