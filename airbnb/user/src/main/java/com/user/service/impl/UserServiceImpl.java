package com.user.service.impl;

import com.user.entity.User;
import com.user.exception.*;
import com.user.payload.ChangeDto;
import com.user.payload.ForgotPasswordDto;
import com.user.payload.LoginDto;
import com.user.payload.UserDto;
import com.user.repo.UserRepository;
import com.user.service.UserService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private JWTService jwtService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }


    @Override
    public User addUser(UserDto userDto) {

        if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("User already exists with username: " + userDto.getUsername());
        }
        else {
            User user= new User();
            // Map the fields from the UserDto to the User entity.
            mapToEntity(user, userDto);
            String randomUserId = UUID.randomUUID().toString();
            user.setUserId(randomUserId);
            user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt(10)));
            User saved = userRepository.save(user);
            return saved;
        }
    }

    @Override
    public List<User> getAll() {
        try {
            List<User> all = userRepository.findAll();
            return all;
        }
        catch (Exception e){
            throw new UserListRetrievalException("Failed to retrieve users list {}");
        }
    }

    @Override
    public User getByUserId(String userId) {
        Optional<User> byId = userRepository.findById(userId);
        if(byId.isPresent()){
            User user = byId.get();
            return user;
        }
        else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public User getByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()){
            User user = byUsername.get();
            return user;
        }
        else{
            throw new UserNotFoundException("User not found with Username: "+username);
        }
    }

    @Override
    public User updateUser(String username, UserDto userDto) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if(byUsername.isPresent()){
            User user = byUsername.get();
            // Map the fields from the userDto to the existing user entity.
            User updatedUser = mapToEntity(user, userDto);
            User saved = userRepository.save(updatedUser);
            return saved;
        } else {
            throw new UserNotFoundException("User not found with Username: " + username);
        }
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()){
            User user = byUsername.get();
            userRepository.deleteByUsername(username);
        }
        else {
            throw new UserNotFoundException("User not found with Username: "+username);
        }
    }

    @Override
    public String verifyLogin(LoginDto loginDto) {
        Optional<User> byUsername = userRepository.findByUsername(loginDto.getUsername());
        if(byUsername.isPresent()){
            User user = byUsername.get();
            if(BCrypt.checkpw(loginDto.getPassword(), user.getPassword())){
                return jwtService.generateToken(user);
            }
            else {
                throw new ValidationException("Validation error: Invalid Credentials");
            }
        }
        else {
            throw new ValidationException("Validation error: Invalid Credentials");
        }
    }

    @Override
    public void changePassword(String userId, ChangeDto changeDto) {
        Optional<User> byId = userRepository.findById(userId);
        User user = byId.get();
        if(BCrypt.checkpw(changeDto.getOldPassword(), user.getPassword())){
            String newPassword = BCrypt.hashpw(changeDto.getNewPassword(), BCrypt.gensalt(10));
            user.setPassword(newPassword);
        }
        else {
            throw new ValidationException("Incorrect Password");
        }
    }

    @Override
    public void forgotPassword(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if(byUsername.isPresent()){
            User user = byUsername.get();

            //logics
        }
        else
            throw new UserNotFoundException("User not found with Username: "+username);

    }


    public User mapToEntity(User user, UserDto userDto) {
        // Configure ModelMapper to map only non-null fields from the userDto to the user entity.
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        // Map the non-null fields from the userDto to the user entity.
        modelMapper.map(userDto, user);

        return user;
    }

}
