package com.pelinhangisi.springboottodoapp.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTodoItemRequest {
    @NotBlank
    private String description;

}
