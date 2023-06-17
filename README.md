# YUICompressor-maven-plugin

[![Java CI](https://github.com/hazendaz/yuicompressor-maven-plugin/workflows/Java%20CI/badge.svg)](https://github.com/hazendaz/yuicompressor-maven-plugin/actions?query=workflow%3A%22Java+CI%22)
[![Coverage Status](https://coveralls.io/repos/github/hazendaz/yuicompressor-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/hazendaz/yuicompressor-maven-plugin?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz.maven/yuicompressor-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz.maven/yuicompressor-maven-plugin)
[![License](http://img.shields.io/:license-glp-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1.html)

![hazendaz](src/site/resources/images/hazendaz-banner.jpg)

## Overview

Maven's plugin to compress (minify/obfuscate/aggregate) JavaScript and CSS files using [YUI Compressor](https://github.com/yui/yuicompressor)

## Documentation

Full documentation is available under following link:  https://davidb.github.io/yuicompressor-maven-plugin/

Summary of the project history can be found in [CHANGELOG](https://github.com/davidB/yuicompressor-maven-plugin/blob/master/CHANGELOG)

## Build

* `./mvnw package` : generate jar
* `./mvnw site` : generate the plugin website
* `./mvnw integration-test` : `./mvnw package` + run all integration test
* `./mvnw integration-test -Dinvoker.test=demo01` : run integration test 'demo01' (against all configuration) useful for tuning/debug
* `./mvnw install` :  `./mvnw integration-test` + publish on local maven repository
* `./mvnw install -Dmaven.test.skip=true` : ./mvnw install` without run of unit test and run of integration test
* release :
  * `gpg --use-agent --armor --detach-sign --output $(mktemp) pom.xml` to avoid issue on macosx with gpg signature see [[MGPG-59] GPG Plugin: "gpg: signing failed: Inap
propriate ioctl for device" - ASF JIRA](https://issues.apache.org/jira/browse/MGPG-59)
  * `./mvnw release:clean && ./mvnw release:prepare && ./mvnw release:perform` : to publish on staging repository via plugin
  * `./mvnw release:clean && ./mvnw release:prepare -Darguments="-DskipTests -Dmaven.test.skip=true" && ./mvnw release:perform -Darguments="-DskipTests -Dmaven.test.skip=true"` to publish without tests
  * `./mvnw site package source:jar javadoc:jar install:install gpg:sign deploy:deploy changes:announcement-generate -Dmaven.test.skip=true -DperformRelease=true` : man
ual
  * connect to http://oss.sonatype.org/ close and release the request(about yuicompressor-maven-plugin) in staging repositories
  * browse the updated [mvnsite](https://davidb.github.io/yuicompressor-maven-plugin/) (check version into samples, ...)

## Issues

Found a bug? Have an idea? Report it to the [issue tracker](https://github.com/davidB/yuicompressor-maven-plugin/issues?state=open)


## Developers

* [David Bernard](https://github.com/davidB)
* [Piotr Kuczynski](https://github.com/pkuczynski)
* [Jeremy Landis](https://github.com/hazendaz)


## License

This project is available under the [Creative Commons GNU LGPL, Version 2.1](http://creativecommons.org/licenses/LGPL/2.1/).
