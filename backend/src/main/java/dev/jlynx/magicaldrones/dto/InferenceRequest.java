package dev.jlynx.magicaldrones.dto;

import jakarta.validation.constraints.Min;

public record InferenceRequest(@Min(1) long transformationId) { }
