package com.user.service;

import com.user.entity.User;
import com.user.payload.ChangeDto;
import com.user.payload.ForgotPasswordDto;
import com.user.payload.LoginDto;
import com.user.payload.UserDto;

import java.util.List;

public interface UserService {

    //add-user
    User addUser(UserDto userDto);

    //get-all-user
    List<User> getAll();

    //get-user-by-id
    User getByUserId(String userId);

    //get-user-by-username
    User getByUsername(String username);

    //update-user
    User updateUser(String username, UserDto userDto);

    //delete-user
    void deleteUser(String username);

    //verify-login
    String verifyLogin(LoginDto loginDto);

    //change-password
    void changePassword(String userId, ChangeDto changeDto);

    //forgot-password
    void forgotPassword(String username);

    //get-user-booking
//    List<Booking> getUserBookings(String userId);
}
