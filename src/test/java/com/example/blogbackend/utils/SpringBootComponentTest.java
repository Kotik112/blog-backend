package com.example.blogbackend.utils;

import static com.example.blogbackend.BlogBackendApplication.API_VERSION_1;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

@AutoConfigureJsonTesters
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SpringBootComponentTest {
  public static final String BASE_BLOG_POST_URL = API_VERSION_1 + "/blog";
  public static final String BASE_COMMENTS_URL = API_VERSION_1 + "/comments";
  public static final String BASE_IMAGE_URL = API_VERSION_1 + "/images";
  public static final String BASE_AUTH_URL = API_VERSION_1 + "/auth";

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  @Autowired protected ObjectMapper objectMapper;

  protected <T> T getFromResult(MvcResult result, Class<T> clazz) {
    try {
      return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    } catch (JsonProcessingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T> List<T> getFromListResult(MvcResult result, Class<T> clazz) {
    try {
      return objectMapper
          .readerForListOf(clazz)
          .readValue(result.getResponse().getContentAsString());
    } catch (JsonProcessingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T> List<T> getFromPageResult(MvcResult result, Class<T> clazz) {
    try {
      JsonNode contentNode =
          objectMapper.readTree(result.getResponse().getContentAsString()).get("content");
      if (contentNode != null && contentNode.isArray()) {
        CollectionType type =
            objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.readValue(contentNode.traverse(), type);
      } else {
        return Collections.emptyList();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
