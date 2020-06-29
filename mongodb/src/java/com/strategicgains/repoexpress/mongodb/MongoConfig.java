package com.strategicgains.repoexpress.mongodb;

import java.util.Properties;

import org.restexpress.common.exception.ConfigurationException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;

public class MongoConfig
{
	private static final String URI_PROPERTY = "mongodb.uri";
	private static final String URI_ENVIRONMENT_PROPERTY = "MONGODB_URI";

	private String dbName;
	private MongoClient client;

	public MongoConfig(Properties p) {
		this(p, null);
	}
	
    public MongoConfig(Properties p, Builder builder)
    {
		String uri = p.getProperty(URI_ENVIRONMENT_PROPERTY);

		if (uri == null)
		{
			uri = p.getProperty(URI_PROPERTY);
		}

		if (uri == null)
		{
			throw new ConfigurationException(String.format("Please define a MongoDB URI for property: %s or %s", URI_PROPERTY, URI_ENVIRONMENT_PROPERTY));
		}

		MongoClientURI mongoUri = new MongoClientURI(uri, (builder != null) ? builder : new MongoClientOptions.Builder());
		dbName = mongoUri.getDatabase();
        client = new MongoClient(mongoUri);
		initialize(p);
    }

    /**
     * Sub-classes can override to initialize other properties.
     * 
     * @param p Propreties
     */
	protected void initialize(Properties p)
    {
		// default is to do nothing.
    }

	public String getDbName()
	{
		return dbName;
	}

	public MongoClient getClient()
	{
		return client;
	}
}
