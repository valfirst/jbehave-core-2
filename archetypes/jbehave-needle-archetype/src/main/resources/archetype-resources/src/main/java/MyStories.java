#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnit4StoryRunner;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.needle.NeedleStepsFactory;
import org.junit.runner.RunWith;
import org.needle4j.injection.InjectionProvider;
import org.needle4j.injection.InjectionProviders;

import ${package}.steps.MySteps;
import ${package}.steps.SpecialService;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML;

/**
 * <p>
 * {@link Embeddable} class to run multiple textual stories via JUnit using Needle Dependency Injection to compose the steps classes.
 * </p>
 * <p>
 * Stories are specified in classpath and correspondingly the {@link LoadFromClasspath} story loader is configured.
 * </p> 
 */
@RunWith(JUnit4StoryRunner.class)
public class MyStories extends JUnitStories {
    
    public MyStories() {
        configuredEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(true)
                .doIgnoreFailureInView(true).useThreads(2).useStoryTimeouts("60");
    }

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();
        Configuration configuration = new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                        .withDefaultFormats()
                        .withFormats(CONSOLE, TXT, HTML, XML));
        // add custom converters
        configuration.parameterConverters().addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")));
        return configuration;
    }
    
    @Override
    public InjectableStepsFactory stepsFactory() {
        final Class<?>[] steps = new Class<?>[] { MySteps.class };

        final Set<InjectionProvider<?>> providers = new HashSet<InjectionProvider<?>>();
        providers.add(InjectionProviders.providerForInstance(new SpecialService() {
            public boolean makeHappy() {
                return false;
            }
        }));
        return new NeedleStepsFactory(configuration(), providers, steps);
    }

    @Override
    public List<String> storyPaths() {
      return new StoryFinder().findPaths(codeLocationFromPath("src/main/resources"), "**/*.story", "");
    }
}
