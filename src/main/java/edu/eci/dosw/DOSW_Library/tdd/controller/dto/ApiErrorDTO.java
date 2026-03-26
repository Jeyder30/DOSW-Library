package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@Schema(description = "Respuesta de error de la API")
public class ApiErrorDTO {
    int status;
    String error;
    String message;
    String path;
    LocalDateTime timestamp;
    List<String> details;
}
