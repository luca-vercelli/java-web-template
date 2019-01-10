package odata.jpa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint.
 * 
 * If you plan to have multiple Application's, you should specify which RestResourcesEndpoint class to use.
 * 
 * Webapps must extend this.
 */
public abstract class AbstractJAXRSApplication extends Application {

	/**
	 * Enable Jersey Multipart feature. Disable MOXY as JSON serializer.
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature");
		return props;
	}
	
	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();

        // Add JacksonFeature.
        //classes.add(JacksonFeature.class);
        classes.add(JacksonFeatureBugfix.class);

        return classes;
    }

}
