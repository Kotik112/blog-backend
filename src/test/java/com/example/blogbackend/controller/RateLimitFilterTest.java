package com.example.blogbackend.controller;

// class RateLimitFilterTest extends SpringBootComponentTest {
//
//  @Autowired MockMvc mvc;
//
//  @Test
//  void when_underLimit_then_requestAllowed() throws Exception {
//    mvc.perform(get(BASE_BLOG_POST_URL)).andExpect(status().isOk());
//  }
//
//  @Test
//  void when_overLimit_then_returnTooManyRequests() throws Exception {
//    MockHttpSession session = new MockHttpSession();
//    // Simulate hitting the rate limit
//    for (int i = 0; i < 105; i++) {
//      mvc.perform(get(BASE_BLOG_POST_URL).session(session))
//          .andExpect(i < 100 ? status().isOk() : status().isTooManyRequests());
//    }
//  }
//
//  @Test
//  @WithMockUser(username = "testuser", roles = "USER")
//  void when_authenticated_then_sessionBasedKeyUsed() throws Exception {
//    MockHttpSession session = new MockHttpSession();
//    // Should use session-based key
//    for (int i = 0; i < 100; i++) {
//      mvc.perform(get("/api/v1/blog/logged-in-user").session(session)).andExpect(status().isOk());
//    }
//
//    mvc.perform(get("/api/v1/blog/logged-in-user").session(session))
//        .andExpect(status().isTooManyRequests());
//  }
//
//  @Test
//  void when_unauthenticated_then_ipBasedKeyUsed() throws Exception {
//    // Make 100 requests successfully (under limit)
//    for (int i = 0; i < 100; i++) {
//      mvc.perform(get(BASE_BLOG_POST_URL)).andExpect(status().isOk());
//    }
//
//    // The 101st request should be rejected due to rate limit
//    mvc.perform(get(BASE_BLOG_POST_URL)).andExpect(status().isTooManyRequests());
//  }
// }
