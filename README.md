# YUICompressor-maven-plugin

[![Java CI](https://github.com/hazendaz/yuicompressor-maven-plugin/workflows/Java%20CI/badge.svg)](https://github.com/hazendaz/yuicompressor-maven-plugin/actions?query=workflow%3A%22Java+CI%22)
[![Coverage Status](https://coveralls.io/repos/github/hazendaz/yuicompressor-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/hazendaz/yuicompressor-maven-plugin?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz.maven/yuicompressor-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz.maven/yuicompressor-maven-plugin)
[![License](http://img.shields.io/:license-glp-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1.html)

![hazendaz](src/site/resources/images/hazendaz-banner.jpg)

## Overview

Maven's plugin to compress (minify/obfuscate/aggregate) JavaScript and CSS files using [YUI Compressor](https://github.com/yui/yuicompressor)

This repository is a restore of the original plugin as it was archived and usage will break on maven 4.0.  The intent is not to build out a lot of new features but rather allow continued usage.  However, project is open to all to contribute features if deemed necessary as well as any bug fixes.

## Documentation

Full documentation is available under following link:  https://davidb.github.io/yuicompressor-maven-plugin/

Summary of the project history can be found in [CHANGELOG](https://github.com/hazendaz/yuicompressor-maven-plugin/blob/master/CHANGELOG.md)

## Build

Regular build
* mvn clean install

Release
* mvn release:clean
* mvn release:prepare
* mvn release:perform

For release and build in general, all items necessary are built into the .mvn folder under settings.xml.  For secure items, those are not included there but it is designed to run snapshots to sonatype through github.

## Issues

Found a bug? Have an idea? Report it to the [issue tracker](https://github.com/hazendaz/yuicompressor-maven-plugin/issues)


## Developers

* [David Bernard](https://github.com/davidB)
* [Piotr Kuczynski](https://github.com/pkuczynski)
* [Jeremy Landis](https://github.com/hazendaz)


## License

This project is available under the [Creative Commons GNU LGPL, Version 2.1](http://creativecommons.org/licenses/LGPL/2.1/).
