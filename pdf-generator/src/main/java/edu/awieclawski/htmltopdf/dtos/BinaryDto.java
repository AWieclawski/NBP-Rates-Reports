package edu.awieclawski.htmltopdf.dtos;

import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BinaryDto {
	String name;
	byte[] bytes;
	MediaType mimeType;
}
