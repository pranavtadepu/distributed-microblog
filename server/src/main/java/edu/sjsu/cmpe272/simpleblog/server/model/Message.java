package edu.sjsu.cmpe272.simpleblog.server.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
@Data
@NoArgsConstructor
public class Message {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "mid")
    Integer id;
    LocalDateTime date;
    String author;
    String message;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String attachment;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String signature;
//    @JoinColumn(name = "usrId_fk")
//    @ManyToOne
//    private User user;
}
