package com.user.controller;

import com.user.entity.User;
import com.user.exception.UserNotFoundException;
import com.user.payload.ChangeDto;
import com.user.payload.LoginDto;
import com.user.payload.TokenResponse;
import com.user.payload.UserDto;
import com.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //add-users
    @PostMapping("/add-users")
    public ResponseEntity<String> addUsers(@RequestBody UserDto userDto){

        logger.info("addUsers method in service started.");
        userService.addUser(userDto);
        logger.info("addUsers method in service has ended.");
        return new ResponseEntity<>("User Registered Successful.", HttpStatus.CREATED);
    }

    //get-list-of-users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        logger.info("getAll method in service started.");
        List<User> all = userService.getAll();
        logger.info("getAll method in service has ended.");
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    //get-user-by-id
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId){
        User byUserId = userService.getByUserId(userId);
        return new ResponseEntity<>(byUserId, HttpStatus.OK);
    }

    //get-user-by-username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        User byUsername = userService.getByUsername(username);
        return new ResponseEntity<>(byUsername, HttpStatus.OK);
    }

    //update-user-details
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal User user, @RequestBody UserDto userDto){

        if (user == null) {
            // Handle authentication failure (e.g., invalid or expired token)
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
        }

        String username = user.getUsername();
        try {
            userService.updateUser(username, userDto);
            return new ResponseEntity<>("User data updated Successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            // Handle case where the authenticated user not found in the system
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions such as validation errors or database errors
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //verify-login
    @PostMapping("/login")
    public ResponseEntity<?> verifyLogin(@RequestBody LoginDto loginDto){
        String token = userService.verifyLogin(loginDto);

        if(token!=null){
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setToken(token);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        }

        else
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
    }

    //get-users-profile
    @PostMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User user){

        if (user == null)
            // Handle authentication failure (e.g., invalid or expired token)
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
        else
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //change-password
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody ChangeDto changeDto){

        if (user == null) {
            // Handle authentication failure (e.g., invalid or expired token)
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
        }

        String userId = user.getUserId();
        userService.changePassword(userId, changeDto);
        return new ResponseEntity<>("Password Change successful", HttpStatus.OK);
    }

    /* perform without using AuthenticationPrincipal
    @PostMapping("/change-password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable String userId, @AuthenticationPrincipal User user,
                                                 @RequestBody ChangeDto changeDto){
        userService.changePassword(user.getUserId(), changeDto);
        return new ResponseEntity<>("Password Change successful", HttpStatus.OK);
    }
    * */

    //forgot-password
    @PostMapping("/forgot-password/{username}")
    public ResponseEntity<String> forgotPassword(@PathVariable String username){
        userService.forgotPassword(username);
        return new ResponseEntity<>("Password reset instructions sent successfully.", HttpStatus.OK);
    }

    //delete-user
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        String username = user.getUsername();
        userService.deleteUser(username);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }


}
