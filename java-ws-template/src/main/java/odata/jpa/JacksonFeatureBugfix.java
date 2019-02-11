package odata.jpa;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.CommonProperties;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * This Feature should enable Jackson and disable MOXY.
 * 
 * AFAIK, Jackson JSON serializer is in the library
 * com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider while the library
 * org.glassfish.jersey.media:jersey-media-json-jackson is just a wrapper, its
 * main class is the Feature org.glassfish.jersey.jackson.JacksonFeature .
 * 
 * Unluckily, such Feature registers the JacksonJaxbJsonProvider.class instead of
 * JacksonJsonProvider.class, I don't know why. That gives a bad exception the
 * first time you invoke the webservice:
 * 
 * org.glassfish.jersey.server.ContainerException:
 * java.lang.NoClassDefFoundError:
 * com/fasterxml/jackson/module/jaxb/JaxbAnnotationIntrospector
 * 
 * There is a number of <b>wrong</b> answers in the web,
 * 
 * @see https://stackoverflow.com/questions/18317927
 * @see https://blog.mikeski.net/blog_post/449
 *
 */
public class JacksonFeatureBugfix implements Feature {

	@Override
	public boolean configure(final FeatureContext context) {

		// disable MOXY. Is this needed?
		String postfix = "";
		postfix = '.' + context.getConfiguration().getRuntimeType().name().toLowerCase();
		context.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE + postfix, true);

		context.register(JsonParseExceptionMapper.class);
		context.register(JsonMappingExceptionMapper.class);

		// context.register(JacksonJaxbJsonProvider.class, MessageBodyReader.class,
		// MessageBodyWriter.class);
		
		// default provider ignores JAXB annotations!
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.setAnnotationIntrospector(introspector);
        JacksonJsonProvider provider = new JacksonJsonProvider(mapper);
        
		context.register(provider, MessageBodyReader.class, MessageBodyWriter.class);
		return true;
	}
}