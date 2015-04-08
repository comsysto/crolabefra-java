package com.comsysto.gradle;
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin;
import me.champeau.gradle.JMHPlugin;
import me.champeau.gradle.JMHPluginExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;


class CroLaBeFraJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        PluginContainer plugins = project.getPlugins();
        plugins.apply(JavaPlugin.class);
        plugins.apply(ShadowPlugin.class);
        plugins.apply(JMHPlugin.class);

        JMHPluginExtension extension = project.getExtensions().findByType(JMHPluginExtension.class);
        extension.setJmhVersion("1.6.3");
        extension.setResultFormat("JSON");
    }
}
