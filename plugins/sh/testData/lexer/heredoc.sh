#!/bin/sh

cat <<EO\+\\F >> /opt/buildAgent/conf/buildAgent.properties
intellij.can.build.default.branch=true
intellij.build.branch.pattern=master
EO+\F

cat <<EO\\F
ginstall: creating directory 'sub3'
EO\F

	cat <<-\EOF
		#
		# THIS FILE IS AUTOGENERATED;
		#
		echo "Example"
	EOF