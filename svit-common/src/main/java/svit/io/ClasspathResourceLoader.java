package svit.io;

import svit.matcher.Matcher;
import svit.util.Files;
import svit.util.Jars;
import svit.util.JavaIO;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * A {@link ResourceLoader} implementation for loading resources from the classpath.
 * <p>
 * This loader handles both JAR-based and file-based resources, delegating resource loading
 * to specialized loaders like {@link JarURLResourceLoader} and {@link FileSystemResourceLoader}.
 * </p>
 */
public class ClasspathResourceLoader extends AbstractResourceLoader {

    /**
     * Loads resources from the classpath matching the specified location and matcher.
     *
     * @param location the resource location in the classpath
     * @param matcher  the matcher to filter resource names
     * @return a collection of {@link Resource} objects matching the criteria
     * @throws ResourceException if an error occurs during resource loading
     */
    @Override
    public Collection<Resource> loadResources(String location, Matcher<String> matcher) {
        Collection<Resource> resources = new ArrayList<>();

        ensureSupportedProtocol(location);

        location = Files.removeProtocol(location);

        try {
            Enumeration<URL> enumeration = getClassLoader().getResources(location);

            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();

                switch (url.getProtocol()) {
                    case Resource.JAR_PROTOCOL:
                        resources.addAll(new JarURLResourceLoader().loadResources(location, url, matcher));
                        break;
                    case Resource.FILE_PROTOCOL:
                        resources.addAll(new FileSystemResourceLoader().loadResources(location, Paths.get(url.toURI()), matcher));
                        break;
                    default:
                        throw new ResourceLoaderException("Protocol '%s' not supported.".formatted(url.getProtocol()));
                }

            }

        } catch (Exception e) {
            throw new ResourceException("Failed to read resources from '%s' files".formatted(location), e);
        }

        return resources;
    }

    /**
     * Loads a single resource from the classpath.
     *
     * @param location the resource location
     * @return the loaded {@link Resource}
     * @throws ResourceException if the resource cannot be resolved
     */
    @Override
    public Resource getResource(String location) {
        return Jars.isJarURL(location)
                ? new JarURLResource(JavaIO.toURL(location, getClassLoader()))
                : new URLResource(JavaIO.toURL(location, getClassLoader()));
    }

    /**
     * Returns the list of supported protocols.
     *
     * @return a list containing "classpath"
     */
    @Override
    public List<String> supportedProtocols() {
        return List.of(Resource.CLASSPATH_PROTOCOL);
    }

}
