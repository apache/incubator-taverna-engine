#!/bin/sh

if [ $# != 2 ]; then
    echo "Usage: $0 packageName activityName"
    echo "  Example: $0 net.sf.taverna.activities.biomart Biomart"
    exit 1
fi

groupId="net.sf.taverna.t2.activities.archetypes"
artifactId="activity-archetype"
version="0.1.0"

myGroupId=$1
myArtifactId=$2

mvn archetype:create -DarchetypeGroupId=$groupId -DarchetypeArtifactId=$artifactId -DarchetypeVersion=$version -DgroupId=$myGroupId -DartifactId=$myArtifactId

for file in `find $myArtifactId -name 'Example*'`
do
    mv $file ${file%%Example*}$myArtifactId${file#*/Example}
done
