package edu.sjsu.cmpe272.simpleblog.server.respository;

import edu.sjsu.cmpe272.simpleblog.server.model.Userdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<Userdata,Integer> {
    Userdata findByName(String name);
}
