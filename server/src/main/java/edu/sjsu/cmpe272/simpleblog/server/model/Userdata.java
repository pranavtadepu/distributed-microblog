package edu.sjsu.cmpe272.simpleblog.server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class Userdata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Column(unique = true)
    String name;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String public_key;
}
