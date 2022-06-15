#!/bin/bash
# Copyright 2022 Google LLC
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

cp -r "/usr/lib/jvm/java-17-openjdk-amd64/" "$JAVA_HOME"

echo 'diff --git a/build.xml b/build.xml
index 2a67d5346f..6c53a163cd 100644
--- a/build.xml
+++ b/build.xml
@@ -219,6 +219,7 @@
     <pathelement location="${jaxrpc-lib.jar}"/>
     <pathelement location="${wsdl4j-lib.jar}"/>
     <pathelement location="${migration-lib.jar}"/>
+    <pathelement location="${unboundid.jar}"/>
   </path>
 
   <path id="tomcat.classpath">
@@ -3180,6 +3181,15 @@ skip.installer property in build.properties" />
       <param name="checksum.value" value="${migration-lib.checksum.value}"/>
     </antcall>
 
+    <antcall target="downloadfile">
+      <param name="sourcefile" value="${unboundid.loc}"/>
+      <param name="destfile" value="${unboundid.jar}"/>
+      <param name="destdir" value="${unboundid.home}"/>
+      <param name="checksum.enabled" value="${unboundid.checksum.enabled}"/>
+      <param name="checksum.algorithm" value="${unboundid.checksum.algorithm}"/>
+      <param name="checksum.value" value="${unboundid.checksum.value}"/>
+    </antcall>
+
   </target>
 
   <target name="download-test-compile"' > patch.diff

git apply patch.diff

$ANT

cd $SRC/tomcat/output/classes && jar cfv classes.jar . && mv ./classes.jar $OUT && cd $SRC/tomcat

cp /root/tomcat-build-libs/unboundid*/unboundid*.jar $OUT/unboundid-ldapsdk.jar

ALL_JARS="classes.jar unboundid-ldapsdk.jar"

# The classpath at build-time includes the project jars in $OUT as well as the
# Jazzer API.
BUILD_CLASSPATH=$(echo $ALL_JARS | xargs printf -- "$OUT/%s:"):$JAZZER_API_PATH

# All .jar and .class files lie in the same directory as the fuzzer at runtime.
RUNTIME_CLASSPATH=$(echo $ALL_JARS | xargs printf -- "\$this_dir/%s:"):\$this_dir

for fuzzer in $(find $SRC -name '*Fuzzer.java'); do
  fuzzer_basename=$(basename -s .java $fuzzer)
  javac -cp $BUILD_CLASSPATH $fuzzer --release 17
  cp $SRC/[$fuzzer_basename]*.class $OUT/

  # Create an execution wrapper that executes Jazzer with the correct arguments.
  echo "#!/bin/sh
# LLVMFuzzerTestOneInput for fuzzer detection.
this_dir=\$(dirname \"\$0\")
JAVA_HOME=\"\$this_dir/open-jdk-17/\" \
LD_LIBRARY_PATH=\"$JVM_LD_LIBRARY_PATH\":\$this_dir \
\$this_dir/jazzer_driver --agent_path=\$this_dir/jazzer_agent_deploy.jar \
--cp=$RUNTIME_CLASSPATH \
--target_class=$fuzzer_basename \
--jvm_args=\"-Xmx2048m\" \
\$@" > $OUT/$fuzzer_basename
  chmod u+x $OUT/$fuzzer_basename
done