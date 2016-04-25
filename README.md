# crolabefra-java
Gradle Extension in order to execute JMH micro benchmarks for _CroLaBeFra (Cross-Language-Benchmarking-Framework)_. See https://github.com/bensteinert/crolabefra-setup-poc for a detailed POC and use case!

Tested with gradle up to version 2.13

## CroLaBeFra integration
This gradle plugin is part of a toolset which is instrumented with a ['mothership'](https://github.com/comsysto/crolabefra-mothership) plugin, that should be applied to a surrounding root project. Check also the POC project mentioned above!

## Usage

    plugins {
        id "com.comsysto.gradle.crolabefra.java" version "0.2.2"
    }
       
This plugin can also be used standalone, but then does not offer any advantage compared to the jmh-gradle-plugin.

    $ gradle runJavaBenchmarks
    
executes all contained JMH benchmarks by making use of the jmh task. Additional settings are applied.

