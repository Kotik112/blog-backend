package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.UserDto;
import com.example.blogbackend.exception.IllegalOperationException;
import com.example.blogbackend.service.BlogPostService;
import com.example.blogbackend.service.UserService;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController handles administrative operations such as managing blog posts and users. It
 * provides endpoints for retrieving all blog posts, deleting posts, retrieving all users, updating
 * user roles, and deleting users. This controller is intended for use by users with administrative
 * privileges.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Validated
public class AdminController {

  private final BlogPostService blogPostService;
  private final UserService userService;

  public AdminController(BlogPostService blogPostService, UserService userService) {
    this.blogPostService = blogPostService;
    this.userService = userService;
  }

  /**
   * Retrieves a paginated list of all blog posts.
   *
   * @param page the page number to retrieve (default is 0)
   * @param size the number of posts per page (default is 10)
   * @return A ResponseEntity containing a Page of BlogPostDto objects
   */
  @GetMapping("/all-posts")
  public ResponseEntity<Page<BlogPostDto>> getAllPosts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Page<BlogPostDto> allBlogPosts = blogPostService.getAllBlogPosts(page, size);
    return ResponseEntity.ok(allBlogPosts);
  }

  /**
   * Deletes a blog post by its ID. If the post does not exist, returns a 404 Not Found response.
   *
   * @param id the ID of the blog post to delete
   * @return A ResponseEntity with no content if deletion is successful, or 404 if the post is not
   *     found
   */
  @DeleteMapping("/posts/{id}")
  public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    if (blogPostService.getBlogPostById(id) == null) {
      return ResponseEntity.notFound().build();
    }
    blogPostService.deletePost(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves a paginated list of all users.
   *
   * @param page the page number to retrieve (default is 0)
   * @param size the number of users per page (default is 10)
   * @return A ResponseEntity containing a Page of UserDto objects
   */
  @GetMapping("/all-users")
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Page<UserDto> allUsers = userService.getAllUsers(page, size);
    return ResponseEntity.ok(allUsers);
  }

  /**
   * Updates the role of a user identified by their username.
   *
   * @param username the username of the user whose role is to be updated
   * @param role the new role to assign to the user
   * @return A ResponseEntity containing the updated UserDto, or 404 if the user is not found
   */
  @PatchMapping("/users/{username}/role")
  public ResponseEntity<UserDto> updateUserRole(
      @PathVariable String username, @RequestParam String role, Principal principal) {
    if (principal.getName().equalsIgnoreCase(username)) {
      throw new IllegalOperationException("Administrators cannot change their own roles.");
    }
    if (!userService.userExists(username)) {
      return ResponseEntity.notFound().build();
    }
    UserDto updatedUser = userService.updateUserRole(username, role);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/users/{username}")
  public ResponseEntity<Void> deleteUser(@PathVariable String username) {
    if (!userService.userExists(username)) {
      return ResponseEntity.notFound().build();
    }
    userService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }
}
