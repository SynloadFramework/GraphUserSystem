package com.synload.graphUserSystem;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;

@Module()
public class GraphUserSystem extends ModuleClass {

	@Override
	public void initialize() {
		Log.info("Loaded Neo4J User Database", GraphUserSystem.class);
		
		Log.info("optimizing user graph database", GraphUserSystem.class);
		IndexDefinition indexDefinition;
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() )
		{
		    Schema schema = SynloadFramework.graphDB.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "User" ) ).on( "username" ).create();
		    tx.success();
		}
	}

	@Override
	public void crossTalk(Object... obj) {
		
	}

}
