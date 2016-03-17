# crolabefra-java
Gradle Extension in order to execute JMH micro benchmarks for _CroLaBeFra (Cross-Language-Benchmarking-Framework)_. More details to come soon! Stay tuned on [cS Blog](http://blog.comsysto.com) as well!

Tested with gradle up to version 2.8

## Usage

    plugins {
        id "com.comsysto.gradle.crolabefra.java" version "0.2.0"
    }
       
This plugin can also be used standalone, but then does not offer any advantage compared to the jmh-gradle-plugin.

    $ gradle runJavaBenchmarks
    
executes all contained JMH benchmarks by making use of the jmh task. Additional settings are applied.

