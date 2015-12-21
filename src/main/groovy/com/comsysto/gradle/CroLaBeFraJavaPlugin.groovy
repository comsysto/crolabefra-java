package com.comsysto.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.champeau.gradle.JMHPlugin
import me.champeau.gradle.JMHPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class CroLaBeFraJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.plugins.apply(JavaPlugin)
        project.plugins.apply(ShadowPlugin)
        project.plugins.apply(JMHPlugin)

        def extension = project.getExtensions().findByType(JMHPluginExtension)
        extension.jmhVersion = "1.11.2"
        extension.resultFormat = "JSON"

        project.tasks.create(
                [
                        name     : 'runJavaBenchmarks',
                        group    : 'crolabefra',
                        dependsOn: 'jmh'
                ],
                {
                    description 'Executes assembled JMH Java benchmarks'
                }
        )

        def mapResultsTask = project.tasks.create(
                [
                        name : 'mapJavaResults',
                        group: 'crolabefra'//,
                        //dependsOn: ['runCppBenchmarks'],
                ],
                {
                    mustRunAfter 'runJavaBenchmarks'
                    description 'Converts JMH benchmark results to CroLaBeFra format'
                }
        )

        mapResultsTask.doFirst {
            File file = new File(project.buildDir, 'reports/jmh/results.txt')
            if (file.exists()) {

                //read JMH result file
                def jmhResults = file.withReader { reader ->
                    new JsonSlurper().parse(reader)
                }

                //conversion code goes here
                def mappedResultList = []

                jmhResults.each { benchmark ->
                    def Map mappedResult = [:]
                    String className = benchmark['benchmark']

                    def packageLimitIdx = className.lastIndexOf('.')
                    if (packageLimitIdx >= 0) {
                        mappedResult.group = className.substring(0, packageLimitIdx)
                        mappedResult.name = className.substring(packageLimitIdx + 1)
                    } else {
                        mappedResult.group = ''
                        mappedResult.name = className
                    }

                    assert benchmark.primaryMetric.scoreUnit == 'ns/op'

                    mappedResult.averageTime = benchmark.primaryMetric.score
                    mappedResult.fastestTime = benchmark.primaryMetric.scorePercentiles['0.0']
                    mappedResult.slowestTime = benchmark.primaryMetric.scorePercentiles['100.0']
                    mappedResult.numberOfIterationsPerRun = benchmark.measurementIterations
                    mappedResult.numberOfRuns = benchmark.forks
                    mappedResult.totalTime = null
                    mappedResult.unit = 'ns'
                    mappedResultList.add(mappedResult)
                }

                // write mapped results back to dest file
                File destFile = new File(project.buildDir, 'results/crolabefra-java.json')
                destFile.getParentFile().mkdirs()
                if (destFile.exists()) {
                    destFile.delete()
                }

                destFile.createNewFile();
                destFile.withWriter('UTF-8', { writer ->
                    writer.write(JsonOutput.prettyPrint(JsonOutput.toJson(mappedResultList)))
                })
            }
        }
    }
}
