package rs.ac.bg.fon.tps_backend.exception;

import lombok.*;

public record ErrorPayload(
  String message
) { }