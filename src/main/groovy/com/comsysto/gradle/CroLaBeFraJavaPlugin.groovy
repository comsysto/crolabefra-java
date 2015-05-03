package com.comsysto.gradle
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
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
        extension.jmhVersion = "1.6.3"
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
    }
}
