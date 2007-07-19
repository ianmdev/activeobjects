/*
 * Copyright 2007 Daniel Spiewak
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *	    http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.ao.blog;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.blog.db.Blog;
import net.java.ao.blog.db.Comment;
import net.java.ao.blog.pages.Index;
import net.java.ao.blog.pages.ViewPost;
import net.java.ao.schema.Generator;

import org.apache.wicket.ISessionFactory;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * @author Daniel Spiewak
 */
public class BlogApplication extends WebApplication {
	private EntityManager manager;
	
	public EntityManager getEntityManager() {
		return manager;
	}
	
	@Override
	protected void init() {
		super.init();
		
		getMarkupSettings().setStripWicketTags(true);
		
		Properties dbProperties = getDBProperties();
		
		String uri = dbProperties.getProperty("db.uri");
		String username = dbProperties.getProperty("db.username");
		String password = dbProperties.getProperty("db.password");
		manager = new EntityManager(DatabaseProvider.getInstance(uri, username, password));
		
		try {
			if (manager.find(Blog.class).length == 0) {
				generateSchema(manager);
			}
		} catch (SQLException e) {
			generateSchema(manager);
		}

		mountBookmarkablePage("/index", Index.class);
		mountBookmarkablePage("/post", ViewPost.class);
	}
	
	private Properties getDBProperties() {
		Properties back = new Properties();
		
		InputStream is = BlogApplication.class.getResourceAsStream("/db.properties");
		
		if (is == null) {
			throw new RuntimeException("Unable to locate db.properties");
		}
		
		try {
			back.load(is);
			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to load db.properties");
		}
		
		return back;
	}
	
	private void generateSchema(EntityManager manager) {
		try {
			Generator.migrate(manager.getProvider(), Comment.class);
			
			manager.create(Blog.class).setName("AO Dogfood Blog");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void destroy() {
		super.destroy();
		
		manager.getProvider().dispose();
	}

	@Override
	public Class<? extends WebPage> getHomePage() {
		return Index.class;
	}

	@Override
	protected ISessionFactory getSessionFactory() {
		return new ISessionFactory() {
			@Override
			public Session newSession(Request request, Response response) {
				return new BlogSession(BlogApplication.this, request);
			}
		};
	}
}
