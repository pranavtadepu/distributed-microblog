package edu.sjsu.cmpe272.simpleblog.server.respository;

import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDAO extends JpaRepository<Message,Integer> {
    @Query(value = "SELECT *" +
            "FROM Message m " +
            "ORDER BY m.mid DESC LIMIT ?1",nativeQuery=true)
    List<Message> findTopNMessages(int limit);

    @Query(value = "SELECT *" +
            "FROM Message m " +
            "WHERE m.mid>= ?1 "+
            "ORDER BY m.mid ASC LIMIT ?2",nativeQuery=true)
    List<Message> findNextNMessages(int messageId, int limit);

    @Query(value = "SELECT *" +
            "FROM Message m " +
            "WHERE m.author = ?1",nativeQuery=true)
    Message findByAuthor(String author);
}
