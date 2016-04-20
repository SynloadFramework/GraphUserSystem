package com.synload.graphUserSystem.relationships;

import org.neo4j.graphdb.RelationshipType;

public enum SessionRel implements RelationshipType
{
	LOGGEDIN,
	LOGGEDOUT,
	LOGGEDINAS
}