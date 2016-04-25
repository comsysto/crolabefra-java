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
        extension.jmhVersion = "1.12"
        extension.resultFormat = "JSON"

        def crolabefraJava = project.tasks.create(
                [
                        name     : 'runJavaBenchmarks',
                        group    : 'crolabefra',
                        dependsOn: 'jmh'
                ],
                {
                    description 'Executes assembled JMH Java benchmarks'
                }
        )


        crolabefraJava.doLast {
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

                    // Probably we need some more converter here ... (ops/s e.g.)
                    assert benchmark.primaryMetric.scoreUnit == 'ms/op'

                    mappedResult.averageTime = benchmark.primaryMetric.score
                    mappedResult.fastestTime = benchmark.primaryMetric.scorePercentiles['0.0']
                    mappedResult.slowestTime = benchmark.primaryMetric.scorePercentiles['100.0']
                    mappedResult.numberOfIterationsPerRun = benchmark.measurementIterations
                    mappedResult.numberOfRuns = benchmark.forks
                    mappedResult.totalTime = null
                    mappedResult.unit = 'ms'
                    mappedResultList.add(mappedResult)
                }

                // write mapped results back to dest files
                File destFile = new File(project.buildDir, 'results/crolabefra-java.json')
                destFile.getParentFile().mkdirs()
                if (destFile.exists()) {
                    destFile.delete()
                }

                destFile.createNewFile();
                destFile.withWriter('UTF-8', { writer ->
                    writer.write(JsonOutput.prettyPrint(JsonOutput.toJson(mappedResultList)))
                })

                // check whether mothership is reachable
                def rootProject = project.getRootProject()
                if (rootProject.getTasksByName('crolabefra', false)) {
                    println('Mothership is there :)')
                    // write mapped results back to dest file
                    File rootDestFile = new File(rootProject.buildDir, 'results/mothership/data/crolabefra-java.js')
                    rootDestFile.getParentFile().mkdirs()
                    if (rootDestFile.exists()) {
                        rootDestFile.delete()
                    }
                    rootDestFile.createNewFile();
                    rootDestFile.withWriter('UTF-8', { writer ->
                        writer.write("crolabefra.data.java = ")
                        writer.write(JsonOutput.prettyPrint(JsonOutput.toJson(mappedResultList)))
                    })
                } else {
                    println('No mothership :(')
                }
            }
        }
    }
}
