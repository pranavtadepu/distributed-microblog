package edu.sjsu.cmpe272.simpleblog.client.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ListMessage {
    int limit=10;
    int next;
}
