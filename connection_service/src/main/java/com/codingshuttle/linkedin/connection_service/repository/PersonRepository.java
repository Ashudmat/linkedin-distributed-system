package com.codingshuttle.linkedin.connection_service.repository;

import com.codingshuttle.linkedin.connection_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository
        extends Neo4jRepository<Person, Long> {

    @Query("""
    MATCH (sender:User {userId: $senderId}),
          (receiver:User {userId: $receiverId})

    MERGE (sender)-[:REQUESTED_CONNECTION]->(receiver)
    """)
    void sendConnectionRequest(
            Long senderId,
            Long receiverId
    );

    @Query("""
    MATCH (sender:User {userId: $senderId})
    -[r:REQUESTED_CONNECTION]->
    (receiver:User {userId: $receiverId})

    DELETE r

    MERGE (sender)-[:CONNECTED_WITH]->(receiver)
    MERGE (receiver)-[:CONNECTED_WITH]->(sender)
    """)
    void acceptConnectionRequest(
            Long senderId,
            Long receiverId
    );

    @Query("""
    MATCH (a:User {userId: $userId1})
    -[r1:CONNECTED_WITH]-
    (b:User {userId: $userId2})

    DELETE r1
    """)
    void removeConnection(
            Long userId1,
            Long userId2
    );

    @Query("""
    MATCH (sender:User {userId: $senderId})
    -[r:REQUESTED_CONNECTION]->
    (receiver:User {userId: $receiverId})

    DELETE r
    """)
    void rejectConnectionRequest(
            Long senderId,
            Long receiverId
    );

    @Query("""
    MATCH (sender:User {userId: $senderId})
    -[r:REQUESTED_CONNECTION]->
    (receiver:User {userId: $receiverId})

    DELETE r
    """)
    void cancelSentRequest(
            Long senderId,
            Long receiverId
    );

    @Query("""
    OPTIONAL MATCH path = shortestPath(
        (a:User {userId: $userId1})
        -[:CONNECTED_WITH*]-
        (b:User {userId: $userId2})
    )

    RETURN COALESCE(length(path),-1)
    """)
    Integer shortestPathLength(
            Long userId1,
            Long userId2
    );

    @Query("""
    MATCH (:User {userId: $userId})
    -[:CONNECTED_WITH]-
    (connection:User)

    WHERE connection.userId <> $userId

    RETURN DISTINCT connection
    """)
    List<Person> findFirstDegreeConnections(
            Long userId
    );

    @Query("""
    MATCH (sender:User)
    -[:REQUESTED_CONNECTION]->
    (receiver:User {userId: $userId})

    RETURN sender
    """)
    List<Person> getPendingRequests(
            Long userId
    );

    @Query("""
    MATCH (:User {userId: $userId})
    -[:CONNECTED_WITH]-
    (friend:User)
    -[:CONNECTED_WITH]-
    (secondDegree:User)
    
    WHERE secondDegree.userId <> $userId
    
    AND NOT EXISTS {
        MATCH (:User {userId: $userId})
        -[:CONNECTED_WITH]-
        (secondDegree)
    }
    
    AND NOT EXISTS {
        MATCH (:User {userId: $userId})
        -[:REQUESTED_CONNECTION]->
        (secondDegree)
    }
    
    AND NOT EXISTS {
        MATCH (secondDegree)
        -[:REQUESTED_CONNECTION]->
        (:User {userId: $userId})
    }
    
    RETURN DISTINCT secondDegree
    """)
    List<Person> getSecondDegreeConnections(
            Long userId
    );

    @Query("""
    MATCH (me:User {userId: $userId1})
    -[:CONNECTED_WITH]-
    (mutual:User)
    -[:CONNECTED_WITH]-
    (other:User {userId: $userId2})

    RETURN DISTINCT mutual
    """)
    List<Person> getMutualConnections(
            Long userId1,
            Long userId2
    );

    Optional<Person> findByUserId(Long userId);

    @Query("""
    MATCH (a:User {userId: $userId1})
    -[:CONNECTED_WITH]-
    (b:User {userId: $userId2})
    
    RETURN COUNT(*) > 0
    """)
    boolean connectionExists(Long userId1, Long userId2);

    @Query("""
    MATCH (sender:User {userId: $senderId})
    -[r:REQUESTED_CONNECTION]->
    (receiver:User {userId: $receiverId})
    
    RETURN COUNT(r) > 0
    """)
    boolean connectionRequestExists(Long senderId, Long receiverId);


}