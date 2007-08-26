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
package net.java.ao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.java.ao.Entity;
import net.java.ao.EntityManager;

/**
 * @author Daniel Spiewak
 */
class EntityType extends DatabaseType<Entity> {

	public EntityType() {
		super(Types.INTEGER, -1, Entity.class);
	}
	
	@Override
	public void putToDatabase(int index, PreparedStatement stmt, Entity value) throws SQLException {
		stmt.setInt(index, value.getID());
	}
	
	@Override
	public Entity convert(EntityManager manager, ResultSet res, Class<? extends Entity> type, String field) throws SQLException {
		return manager.get(type, res.getInt(field));
	}

	@Override
	public String getDefaultName() {
		return "INTEGER";
	}
}