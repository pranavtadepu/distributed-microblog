package edu.sjsu.cmpe272.simpleblog.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ListMessage {
    @Max(value = 20, message = "limit should be less than 20")
    @NotNull(message = "Limit cannot be null")
    int limit=10;
    int next;
}
