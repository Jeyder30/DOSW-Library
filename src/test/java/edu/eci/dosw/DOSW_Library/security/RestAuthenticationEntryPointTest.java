package edu.eci.dosw.DOSW_Library.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RestAuthenticationEntryPointTest {

    @Test
    void commenceShouldWriteAnUnauthorizedApiErrorResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/auth/login");

        entryPoint.commence(request, response, new BadCredentialsException("bad credentials"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals(401, body.get("status").asInt());
        assertEquals("Unauthorized", body.get("error").asText());
        assertEquals("Authentication is required or token is invalid", body.get("message").asText());
        assertEquals("/auth/login", body.get("path").asText());
        assertFalse(body.get("details").elements().hasNext());
    }
}
