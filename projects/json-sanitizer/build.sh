#!/bin/bash -eu
# Copyright 2021 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
################################################################################

# Build the json-sanitizer jar.
echo "Using $JAVA_HOME"
CURRENT_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \
-Dexpression=project.version -q -DforceStdout)
mvn package
cp "target/json-sanitizer-$CURRENT_VERSION.jar" $OUT/json-sanitizer.jar

# The jar files containing the project (separated by spaces).
PROJECT_JARS=json-sanitizer.jar

# Get the fuzzer dependencies (gson).
mvn dependency:copy -Dartifact=com.google.code.gson:gson:2.8.6 -DoutputDirectory=$OUT/

# The jar files containing further dependencies of the fuzz targets (separated
# by spaces).
FUZZER_JARS=gson-2.8.6.jar

# Build fuzzers in $OUT.
ALL_JARS="$PROJECT_JARS $FUZZER_JARS"
BUILD_CLASSPATH=$(echo $ALL_JARS | xargs printf -- "$OUT/%s:"):$JAZZER_API_CLASSPATH

# All jars and class files lie in the same directory as the fuzzer at runtime.
RUNTIME_CLASSPATH=$(echo $ALL_JARS | xargs printf -- "%s:"):.

for fuzzer in $(find $SRC -name '*Fuzzer.java'); do
  fuzzer_basename=$(basename -s .java $fuzzer)
  $JAVA_HOME/bin/javac -cp $BUILD_CLASSPATH $fuzzer
  cp $SRC/$fuzzer_basename.class $OUT/

  # Create execution wrapper.
  echo "#!/bin/sh
# LLVMFuzzerTestOneInput for fuzzer detection.
this_dir=\$(dirname \"\$0\")
ASAN_OPTIONS=\$ASAN_OPTIONS:symbolize=1:external_symbolizer_path=\$this_dir/llvm-symbolizer \
jazzer --cp=$RUNTIME_CLASSPATH:\$this_dir \
       --target_class=$fuzzer_basename \
       --jvm_args=\"-Xmx2048m\" \
       \$@" > $OUT/$fuzzer_basename
  chmod u+x $OUT/$fuzzer_basename
done
