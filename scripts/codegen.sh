#!/bin/bash
#
# Executes the codegen.
#
# Copyright 2016 Mohiva Organisation (license at mohiva dot com)
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

source $(SCRIPTS_DIR)/functions.sh

PID=$$
TIMESTAMP=$(date +%s)
PROJECT_DIR=$(readlink -f "$SCRIPTS_DIR/../")
STUB_DIR="${PROJECT_DIR}/clientstub/src/main/scala/com/mohiva/swagger/codegen"
TMP_DIR="/tmp/swagger-play-scala-generator-$PID$TIMESTAMP"

CODEGEN_ORIGINAL="${PROJECT_DIR}/swagger-codegen-cli.jar"
CODEGEN_CUSTOM="${PROJECT_DIR}/codegen/target/classes"
CLASS_PATH=${CODEGEN_ORIGINAL}:${CODEGEN_CUSTOM}

TEMPLATES="${PROJECT_DIR}/codegen/src/main/resources/play-scala"
LANGUAGE="io.swagger.codegen.languages.PlayScalaClientCodegen"

# Prepare the codegen templates
mkdir -p ${TMP_DIR}/templates
cp ${TEMPLATES}/* ${TMP_DIR}/templates
cp ${STUB_DIR}/core/ApiConfig.scala ${TMP_DIR}/templates/apiConfig.mustache
cp ${STUB_DIR}/core/ApiImplicits.scala ${TMP_DIR}/templates/apiImplicits.mustache
cp ${STUB_DIR}/core/ApiInvoker.scala ${TMP_DIR}/templates/apiInvoker.mustache
cp ${STUB_DIR}/core/ApiRequest.scala ${TMP_DIR}/templates/apiRequest.mustache
cp ${STUB_DIR}/core/ApiResponse.scala ${TMP_DIR}/templates/apiResponse.mustache

# Replace the hardcoded package names with the variable package names
sed -i "s/com.mohiva.swagger.codegen.core/{{invokerPackage}}/g" ${TMP_DIR}/templates/apiConfig.mustache
sed -i "s/com.mohiva.swagger.codegen.core/{{invokerPackage}}/g" ${TMP_DIR}/templates/apiImplicits.mustache
sed -i "s/com.mohiva.swagger.codegen.core/{{invokerPackage}}/g" ${TMP_DIR}/templates/apiInvoker.mustache
sed -i "s/com.mohiva.swagger.codegen.core/{{invokerPackage}}/g" ${TMP_DIR}/templates/apiRequest.mustache
sed -i "s/com.mohiva.swagger.codegen.core/{{invokerPackage}}/g" ${TMP_DIR}/templates/apiResponse.mustache

# Compile the codegen module
${SCRIPTS_DIR}/sbt.sh codegen/compile

# Execute the codegen package
if [ ! -f "$CODEGEN_ORIGINAL" ]
then
    die "Please download the codegen cli"
fi

export JAVA_OPTS="${JAVA_OPTS:-} -Xmx1024M"
java -cp ${CLASS_PATH} ${JAVA_OPTS} io.swagger.codegen.Codegen generate -t ${TMP_DIR}/templates -l "${LANGUAGE}" $@
