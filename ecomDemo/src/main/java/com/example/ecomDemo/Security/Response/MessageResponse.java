package com.example.ecomDemo.Security.Response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MessageResponse {

    @Getter
    @Setter
    String message;

   public MessageResponse(String message){
        this.message=message;
    }


}
